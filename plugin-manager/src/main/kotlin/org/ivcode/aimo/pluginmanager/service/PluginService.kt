package org.ivcode.aimo.pluginmanager.service

import org.ivcode.aimo.plugin.PluginEntryPoint
import org.ivcode.aimo.pluginmanager.info.PluginInfo
import org.ivcode.aimo.pluginmanager.info.PluginInfoService
import org.ivcode.aimo.pluginmanager.loader.PluginLoaderService
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap

/**
 * Service for managing plugins including enabling, disabling, and retrieving plugin information.
 *
 * @param infoService Service for accessing plugin metadata.
 * @param loaderService Service for loading plugin entry points.
 */
@Service
class PluginService (
    private val infoService: PluginInfoService,
    private val loaderService: PluginLoaderService
) {

    /**
     * Map of currently active plugin entry points keyed by plugin id.
     */
    private val activePlugins = ConcurrentHashMap<String, PluginEntryPoint>()

    /**
     * Return a list of all known plugins' metadata.
     *
     * @return list of all [PluginInfo].
     */
    fun getPluginInfo(): List<PluginInfo> {
        return infoService.getPlugins().values.toList()
    }

    /**
     * Return a list of plugin metadata for plugins that are currently enabled.
     *
     * @return list of enabled [PluginInfo].
     */
    fun getEnabledPluginInfo(): List<PluginInfo> {
        return infoService.getPlugins().values.filter { it.enabled }
    }

    /**
     * Return the currently active plugin entry points.
     *
     * @return list of active [PluginEntryPoint].
     */
    fun getActivePlugins(): List<PluginEntryPoint> {
        return activePlugins.values.toList()
    }

    /**
     * Return the active plugin entry point for the given id, or null if not active.
     *
     * @param id plugin identifier.
     * @return the active [PluginEntryPoint] for the given id, or `null` if not active.
     */
    fun getActivePlugin(id: String): PluginEntryPoint? {
        return activePlugins[id]
    }

    /**
     * Enable the plugin identified by the given id and return its active entry point.
     *
     * @param id plugin identifier to enable.
     * @return the active [PluginEntryPoint] for the plugin.
     * @throws IllegalArgumentException if no plugin metadata exists for the given id.
     */
    fun enablePlugin(id: String) = activePlugins.computeIfAbsent(id) {
        // make sure the plugin is known
        val info = infoService.getPlugin(id) ?: throw IllegalArgumentException("Plugin with id '$id' not found")

        // load the entry point
        val entry = loaderService.loadPlugin(id)

        // save the enabled state
        infoService.savePlugin(info.copy(enabled = true))

        entry
    }

    /**
     * Disable the plugin identified by the given id and update its stored state.
     *
     * @param id plugin identifier to disable.
     * @throws IllegalArgumentException if no plugin metadata exists for the given id.
     */
    fun disablePlugin(id: String) {
        // make sure the plugin is known
        val info = infoService.getPlugin(id) ?: throw IllegalArgumentException("Plugin with id '$id' not found")

        // remove and shutdown the active plugin
        activePlugins.remove(id)?.shutdown()

        // save the enabled state
        infoService.savePlugin(info.copy(enabled = false))
    }
}