package org.ivcode.aimo.pluginmanager.loader

import org.ivcode.aimo.plugin.PluginEntryPoint
import org.ivcode.common.data.storage.Storage
import org.springframework.stereotype.Component

private const val PLUGIN_PREFIX = "plugins"

@Component
class PluginLoaderService (
    private val pluginStorage: Storage,
    private val pluginEntryPointFactory: PluginEntryPointFactory,
    private val classloaderManager: PluginClassloaderManager,
) {
    fun loadPlugin (id: String): PluginEntryPoint {
        val classloader = classloaderManager.get(id) { _ ->
            val storagePath = createStoragePath(id)
            pluginStorage.read(storagePath) ?: throw IllegalStateException("Plugin data not found for id: $id")
        }

        val impl = classloader.loadPluginClass()
        return pluginEntryPointFactory.createPluginEntryPoint(impl)
    }

    private fun createStoragePath(id: String): String = "$PLUGIN_PREFIX/$id.jar"
}