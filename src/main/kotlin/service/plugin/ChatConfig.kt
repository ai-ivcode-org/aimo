package org.ivcode.beeboop.service.plugin

import org.ivcode.beeboop.plugin.PluginSettings

data class ChatConfig (
    val chat: SessionPluginConfig,
    val tools: List<SessionPluginConfig>,
)

data class SessionPluginConfig (
    val pluginId: String,
    val settings: PluginSettings,
)

