package org.ivcode.beeboop.pluginman

import org.ivcode.beeboop.datastore.storage.Storage

private const val PLUGIN_PREFIX = "plugins"


class PluginLoader (
    private val pluginStorage: Storage,
    private val pluginClassLoader: PluginClassloader,
    private val pluginFactory: PluginFactory,
) {

    fun <T> getPlugin(type: PluginType<T>, id: String): T? {
        val metadata = pluginClassLoader.getPluginMetadata(type, id) ?: run {
            // if the metadata is not already loaded, attempt to load from storage
            loadPluginFromStorage(type, id)
        }

        val clazz = pluginClassLoader.loadClass(metadata.entryPoint)
        return if (type.instanceOf.isAssignableFrom(clazz)) {
            pluginFactory.createInstance(type, clazz)
        } else {
            throw IllegalArgumentException("Plugin with ID '$id' is not of type '${type.text}'")
        }
    }

    private fun loadPluginFromStorage(type: PluginType<*>, id: String): PluginMeta {
        val storagePath = createStoragePath(type, id)
        val pluginData = pluginStorage.read(storagePath)
        if (pluginData != null) {
            return pluginClassLoader.addPluginPackage(id, pluginData)
        } else {
            // if thrown, the plugin is not available in classloader or storage
            throw IllegalArgumentException("Plugin package for ID '$id' not found in storage")
        }
    }

    private fun createStoragePath(type: PluginType<*>, id: String): String = "$PLUGIN_PREFIX/${type.text}/$id"
}