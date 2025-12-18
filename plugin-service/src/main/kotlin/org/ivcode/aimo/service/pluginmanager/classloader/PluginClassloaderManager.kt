package org.ivcode.aimo.service.pluginmanager.classloader

import org.ivcode.aimo.service.pluginmanager.loader.PluginCacheManager
import org.ivcode.aimo.service.pluginmanager.model.Metadata
import org.ivcode.aimo.service.pluginmanager.model.PluginInfo
import org.ivcode.aimo.service.pluginmanager.utils.toFilename
import org.springframework.stereotype.Component
import java.io.InputStream
import java.net.URL
import java.util.concurrent.ConcurrentHashMap
import kotlin.io.path.exists

/**
 * Manages creation and lifecycle of plugin classloaders backed by plugin package files.
 *
 * Purpose:
 * - Maintain a cache of `PluginClassloader` instances keyed by plugin id.
 * - Persist plugin package bytes to disk and produce a `file:` URL for classloader use.
 * - Close managed classloaders when the manager is closed.
 *
 * @param directory Directory where plugin package files will be written.
 * @param mapper Jackson ObjectMapper used by created classloaders. Defaults to the
 *               Kotlin-friendly jacksonObjectMapper().
 */
@Component
internal class PluginClassloaderManager(
    private val localPackageManager: PluginCacheManager,
    private val allowedPackages: Set<String> = emptySet(),
): AutoCloseable {

    /**
     * Cache of plugin id -> PluginClassloader for active plugins.
     */
    private val classLoaders = ConcurrentHashMap<String, PluginClassloader>()

    /**
     * Obtain the PluginClassloader for the given plugin id.
     *
     * If a classloader already exists for the id it is returned. Otherwise the
     * provided [fallback] function is invoked to obtain an InputStream for the
     * plugin package, the package is saved to disk, and a new PluginClassloader
     * is created and cached.
     *
     * @param id The plugin identifier used as the cache key and to name the saved file.
     * @param fallback Function invoked to supply an InputStream of the plugin package when a
     *                 new classloader must be created.
     * @return a PluginClassloader instance for the plugin id.
     */
    fun get (
        info: PluginInfo,
        fallback: (id: String) -> InputStream
    ): PluginClassloader = classLoaders.computeIfAbsent(info.metadata.id) {
        val file = localPackageManager.getPath(info.storageKey)

        if(file.exists()) {
            PluginClassloader(file.toUri().toURL(), info.metadata, allowedPackages)
        } else {
            val url = fallback(info.metadata.id).use { input ->
                loadPluginPackage(info.metadata, input)
            }

            PluginClassloader(url, info.metadata, allowedPackages)
        }
    }

    fun close (id: String) = classLoaders.remove(id)?.close()

    /**
     * Persist the plugin package stream to the configured [directory] and return
     * a file URL pointing to the saved package. This saves locally only.
     *
     * The method ensures the directory exists and writes the contents of [input]
     * to a file named "<id>.jar".
     *
     * @param id The plugin identifier used to derive the destination filename.
     * @param input InputStream providing the plugin package bytes.
     * @return URL pointing to the saved plugin package file.
     */
    private fun loadPluginPackage (
        metadata: Metadata,
        input: InputStream,
    ): URL {
        return localPackageManager.savePluginPackage(toFilename(metadata), input, overwrite = false).url
    }

    /**
     * Close this manager and release resources held by all managed classloaders.
     * After calling this method the manager should not be used to obtain new
     * classloaders.
     */
    override fun close() {
        classLoaders.values.forEach { it.close() }
    }
}