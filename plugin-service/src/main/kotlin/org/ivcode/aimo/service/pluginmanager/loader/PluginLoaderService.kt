package org.ivcode.aimo.service.pluginmanager.loader

import org.ivcode.aimo.plugin.PluginEntryPoint
import org.ivcode.aimo.service.pluginmanager.LOCK_SESSION
import org.ivcode.aimo.service.pluginmanager.PLUGIN_STORAGE
import org.ivcode.aimo.service.pluginmanager.classloader.PluginClassloaderManager
import org.ivcode.aimo.service.pluginmanager.factory.PluginEntryPointFactory
import org.ivcode.aimo.service.pluginmanager.info.PluginInfoService
import org.ivcode.aimo.service.pluginmanager.model.PluginInfo
import org.ivcode.aimo.service.pluginmanager.scanner.tryLock
import org.ivcode.aimo.service.pluginmanager.utils.toFilename
import org.ivcode.common.data.session.Session
import org.ivcode.common.data.storage.Storage
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import java.io.InputStream

/**
 * @param lock This is a distributed lock used to prevent concurrent writes on a given plugin. The resources in question are the [PluginInfoService] and the [String] representing the plugin's repository
 */
@Component
internal class PluginLoaderService (
    private val pluginEntryPointFactory: PluginEntryPointFactory,
    private val classloaderManager: PluginClassloaderManager,
    private val pluginCacheManager: PluginCacheManager,
    @param:Qualifier(LOCK_SESSION) val lock: Session<String>,
    @param:Qualifier(PLUGIN_STORAGE) private val pluginRepository: Storage,
    private val pluginInfoService: PluginInfoService,
) {

    fun refreshPlugin (path: String): Boolean = lock.tryLock(path,
        // lock acquired
        acquired = {
            val info = pluginInfoService.getPlugin(path)
            val packageExists = pluginRepository.exists(path)

            if(info != null && !packageExists) {
                // plugin package missing, remove plugin info
                removePlugin(info)
            } else if(info == null && packageExists) {
                val input = pluginRepository.read(path) ?: throw IllegalStateException("Failed to read plugin package for path: $path")
                val localInfo = pluginCacheManager.savePluginPackage(path, input)

                // save plugin info as disabled
                pluginInfoService.savePlugin(PluginInfo(
                    storageKey = path,
                    metadata = localInfo.metadata,
                    enabled = false
                ))
                // nothing to do, plugin package missing and no plugin info
                true
            } else {
                // nothing to do, plugin info and package both exist
                true
            }
        },

        // lock unavailable
        unavailable = {
            // refresh failed
            false
        }
    )

    /**
     * If no package exists a defined plugin, remove the plugin info entry and any local cached data
     *
     * _Assumes the lock is held for the plugin being removed._
     */
    private fun removePlugin (info: PluginInfo): Boolean {
        // remove the plugin info
        pluginInfoService.deletePlugin(info.metadata.id)

        // remove the classloader and cached package
        classloaderManager.close(info.metadata.id)
        pluginCacheManager.deletePackage(info.storageKey, ifExists = true)

        return true
    }

    /**
     * Assumes the lock is held for the plugin being added.
     */
    private fun addPlugin (path: String): Boolean {
        // download the plugin package
        val input = pluginRepository.read(path) ?: throw IllegalStateException("Failed to read plugin package for path: $path")

        // add it to the local cache and extract metadata
        val localInfo = pluginCacheManager.savePluginPackage(path, input)

        // save plugin info as disabled
        pluginInfoService.savePlugin(
            PluginInfo(
                storageKey = path,
                metadata = localInfo.metadata,
                enabled = false
            )
        )

        return true
    }

    /**
     * Load the plugin identified by [id] and return its entry point instance.
     *
     * Steps performed:
     * 1. Obtain a `PluginClassloader` for the plugin; if necessary the classloader
     *    will be created by reading the plugin package bytes from [pluginStorage].
     * 2. Load the plugin implementation class from the classloader.
     * 3. Create and return a `PluginEntryPoint` using [pluginEntryPointFactory].
     *
     * @param id Plugin identifier.
     * @return an instantiated `PluginEntryPoint` for the plugin.
     * @throws IllegalStateException if plugin data cannot be read from storage.
     */
    fun loadPlugin (id: String): PluginEntryPoint {
        val info = pluginInfoService.getPlugin(id) ?: throw IllegalStateException("Plugin info not found for id: $id")

        val classloader = classloaderManager.get(info) { _: String ->
            val storagePath = createStoragePath(info.storageKey)
            pluginRepository.read(storagePath) ?: throw IllegalStateException("Plugin data not found for id: $id")
        }

        val impl = classloader.loadPluginClass()
        return pluginEntryPointFactory.createPluginEntryPoint(impl)
    }

    fun uploadPlugin (
        input: InputStream,
        enabled: Boolean = false,
    ): PluginInfo {
        val localInfo = pluginCacheManager.savePluginPackage(input, overwrite = false, lazyFilename = ::toFilename)
        val filename = toFilename(localInfo.metadata)

        // Use a lock to prevent concurrent uploads of the same plugin. Fails with exception if already locked.
        return lock.tryLock(filename) {
            pluginRepository.upsert(createStoragePath(filename), localInfo.url.openStream())

            val pluginInfo = PluginInfo(
                storageKey = filename,
                metadata = localInfo.metadata,
                enabled = enabled,
            )

            pluginInfoService.savePlugin(pluginInfo)
            pluginInfo
        }
    }

    /**
     * Construct the storage path for a plugin package given its [id].
     *
     * @param id Plugin identifier.
     * @return storage path string where the plugin package is stored.
     */
    private fun createStoragePath(filename: String): String = filename
}