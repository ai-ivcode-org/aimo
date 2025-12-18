package org.ivcode.aimo.service.pluginmanager.info

import org.ivcode.aimo.service.pluginmanager.model.PluginInfo

internal class ReadOnlyPluginInfoService(
    private val service: PluginInfoService
): PluginInfoService {
    override fun getPlugins(): Map<String, PluginInfo> {
        return service.getPlugins()
    }

    override fun getPlugin(id: String): PluginInfo? {
        return service.getPlugin(id)
    }

    override fun savePlugin(entry: PluginInfo) {
        throw UnsupportedOperationException("Read only plugin info service")
    }

    override fun deletePlugin(id: String) {
        throw UnsupportedOperationException("Read only plugin info service")
    }
}