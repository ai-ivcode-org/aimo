package org.ivcode.aimo.service.pluginmanager.info

import org.ivcode.aimo.service.pluginmanager.model.PluginInfo
import org.ivcode.common.data.datastore.Datastore

internal class PluginInfoServiceDatastore (
    private val datastore: Datastore<PluginInfo>
): PluginInfoService {
    /**
     * Retrieve all known plugins' metadata.
     *
     * @return a map of plugin id to [PluginInfo] for every plugin stored in the datastore.
     */
    override fun getPlugins(): Map<String, PluginInfo> {
        return datastore.list()
    }

    /**
     * Retrieve metadata for a single plugin by id.
     *
     * @param id the identifier of the plugin to retrieve.
     * @return the [PluginInfo] for the given id, or `null` if no such plugin exists.
     */
    override fun getPlugin(id: String): PluginInfo? {
        return datastore.get(id)
    }

    /**
     * Persist the provided plugin metadata.
     *
     * If an entry with the same id already exists it will be replaced.
     *
     * @param entry the [PluginInfo] to save; its `metadata.id` field is used as the datastore key.
     */
    override fun savePlugin(entry: PluginInfo) {
        datastore.put(entry.metadata.id, entry)
    }

    override fun deletePlugin(id: String) {
        datastore.delete(id)
    }
}