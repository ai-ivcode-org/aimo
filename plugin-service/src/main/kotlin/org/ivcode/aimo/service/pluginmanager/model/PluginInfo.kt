package org.ivcode.aimo.service.pluginmanager.model

data class PluginInfo (
    val metadata: Metadata,
    val storageKey: String,
    val enabled: Boolean,
)