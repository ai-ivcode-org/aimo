package org.ivcode.aimo.plugin

import org.ivcode.aimo.plugin.tool.ToolPlugin

interface PluginEntryPoint {
    fun configure() : ToolPlugin
    fun shutdown()
}