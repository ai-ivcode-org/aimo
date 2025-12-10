package org.ivcode.aimo.pluginmanager.info

import org.ivcode.common.data.datastore.Datastore
import org.springframework.stereotype.Component

@Component
class PluginInfoService(
    private val datastore: Datastore<PluginInfo>
) {
    fun getPlugins(): Map<String, PluginInfo> {
        return datastore.list()
    }

    fun getPlugin(id: String): PluginInfo? {
        return datastore.get(id)
    }

    fun savePlugin(entry: PluginInfo) {
        datastore.put(entry.metadata.id, entry)
    }
}