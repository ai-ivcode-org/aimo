package org.ivcode.aimo.service.pluginmanager.info

import org.ivcode.aimo.service.pluginmanager.model.PluginInfo
import org.ivcode.common.data.datastore.Datastore
import org.springframework.stereotype.Component

interface PluginInfoService {
    /**
     * Retrieve all known plugins' metadata.
     *
     * @return a map of plugin id to [PluginInfo] for every plugin stored in the datastore.
     */
    fun getPlugins(): Map<String, PluginInfo>

    /**
     * Retrieve metadata for a single plugin by id.
     *
     * @param id the identifier of the plugin to retrieve.
     * @return the [PluginInfo] for the given id, or `null` if no such plugin exists.
     */
    fun getPlugin(id: String): PluginInfo?

    /**
     * Persist the provided plugin metadata.
     *
     * If an entry with the same id already exists it will be replaced.
     *
     * @param entry the [PluginInfo] to save; its `metadata.id` field is used as the datastore key.
     */
    fun savePlugin(entry: PluginInfo)

    fun deletePlugin(id: String)
}