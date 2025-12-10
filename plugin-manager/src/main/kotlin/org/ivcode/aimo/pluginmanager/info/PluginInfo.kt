package org.ivcode.aimo.pluginmanager.info

import org.ivcode.aimo.pluginmanager.loader.PluginMeta

data class PluginInfo (
    val metadata: PluginMeta,
    val enabled: Boolean
)