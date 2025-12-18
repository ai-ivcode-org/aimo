package org.ivcode.aimo.service.pluginmanager

import org.ivcode.aimo.plugin.PluginEntryPoint
import org.ivcode.aimo.service.pluginmanager.model.PluginInfo
import org.ivcode.aimo.service.pluginmanager.info.PluginInfoService
import org.ivcode.aimo.service.pluginmanager.loader.PluginLoaderService
import org.ivcode.aimo.service.pluginmanager.scanner.PluginScanner
import org.springframework.stereotype.Service
import java.io.InputStream
import java.util.concurrent.ConcurrentHashMap

/**
 * Service for managing plugins including enabling, disabling, and retrieving plugin information.
 *
 * @param infoService Service for accessing plugin metadata.
 * @param loaderService Service for loading plugin entry points.
 */
@Service
class PluginManagerService internal constructor (
    private val infoService: PluginInfoService,
    private val loaderService: PluginLoaderService,
    private val pluginScanner: PluginScanner
) {

    init {
        scan()
    }

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

    fun getPluginInfo(id: String): PluginInfo? {
        return infoService.getPlugin(id)
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

    fun uploadPlugin(
        input: InputStream,
        enabled: Boolean = false,
    ): PluginInfo {
        val pluginInfo = loaderService.uploadPlugin (
            input = input,
            enabled = enabled,
        )

        return pluginInfo
    }

    /**
     * Trigger a package scan for available plugins.
     *
     * Requests a rescan of plugin packages so newly added, changed, or removed
     * plugin artifacts are discovered and the manager's state can be updated.
     */
    fun scan() {
        pluginScanner.scanPlugins()
    }
}