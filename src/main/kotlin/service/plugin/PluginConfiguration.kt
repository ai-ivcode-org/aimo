package org.ivcode.beeboop.service.plugin

import org.ivcode.beeboop.plugin.PluginSettings

data class PluginConfiguration (
    val plugins: Map<String, Plugin>
) {
    class Plugin (
        val enabled: Boolean,
        val settings: PluginSettings
    )
}