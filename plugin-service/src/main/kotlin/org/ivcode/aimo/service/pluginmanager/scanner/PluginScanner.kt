package org.ivcode.aimo.service.pluginmanager.scanner

import org.ivcode.aimo.service.pluginmanager.loader.PluginCacheManager
import org.ivcode.aimo.service.pluginmanager.scanner.PluginLock
import org.ivcode.aimo.service.pluginmanager.model.PluginInfo
import org.springframework.stereotype.Component

@Component
internal class PluginScanner (
    val lock: PluginLock,
    val localPackageManager: PluginCacheManager
) {

    @Synchronized
    fun scanPlugins() {
        val knownKeys = lock.pluginInfoService.getPlugins().values.map { it.storageKey }.toMutableSet()
        val packagePaths = lock.pluginStorage.listPaths().toMutableList()

        // add plugin info if a package is found
        packagePaths.forEach { path ->
            if (!knownKeys.contains(path)) {
                if(tryAddPlugin(path)) {
                    knownKeys.add(path)
                }
            }
        }

        // remove plugin info if a package is missing
        knownKeys.forEach { key ->
            if(!packagePaths.contains(key)) {
                if(tryRemovePlugin(key)) {
                    knownKeys.remove(key)
                }
            }
        }

        // remove any cached packages not defined in the plugin info
        localPackageManager.listPackagePaths().forEach { packagePath ->
            if(!knownKeys.contains(packagePath)) {
                // delete the local package, but do not error out if the package is missing
                localPackageManager.deletePackage(packagePath, ifExists = true)
            }
        }
    }

    fun tryRemovePlugin(path: String): Boolean = lock.tryLock(path,
        // lock acquired
        acquired = { infoService, _ ->
            // remove the plugin info
            infoService.deletePlugin(path)
            true
        },

        // lock unavailable
        unavailable = {
            // Do nothing. Another process may be handling it, or we can pick it up on the next scan
            false
        }
    )

    fun tryAddPlugin(path: String): Boolean = lock.tryLock(path,
        // lock acquired
        acquired = { infoService, storage ->
            // download plugin
            val input = storage.read(path) ?: throw IllegalArgumentException("The path $path does not exist")
            val localInfo = localPackageManager.savePluginPackage(path, input)

            // save plugin info as disabled
            infoService.savePlugin(PluginInfo(
                storageKey = path,
                metadata = localInfo.metadata,
                enabled = false
            ))

            true
        },

        // lock unavailable
        unavailable = {
            // Do nothing. Another process may be handling it, or we can pick it up on the next scan
            false
        }
    )
}
