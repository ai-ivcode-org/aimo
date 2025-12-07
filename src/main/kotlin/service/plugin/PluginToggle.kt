package org.ivcode.beeboop.service.plugin

import org.ivcode.beeboop.plugin.Plugin

data class PluginToggle <T: Plugin> (
    val plugin: T,
    var enabled: Boolean = false
)