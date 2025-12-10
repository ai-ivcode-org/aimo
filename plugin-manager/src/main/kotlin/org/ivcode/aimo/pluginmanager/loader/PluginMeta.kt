package org.ivcode.aimo.pluginmanager.loader

data class PluginMeta (
    val id: String,
    val name: String,
    val description: String?,
    val version: String?,
    val entryPoint: String,
)
