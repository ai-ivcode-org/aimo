package org.ivcode.beeboop.pluginman

import org.ivcode.beeboop.plugin.chat.ChatPlugin
import org.ivcode.beeboop.plugin.tool.ToolPlugin

class PluginType<T> (val text: String, val instanceOf: Class<T>, val factory: (impl: Class<out T>) -> T) {

    companion object {
        val CHAT = PluginType (
            text = "chat",
            instanceOf = ChatPlugin::class.java,
            factory = { impl -> impl.getDeclaredConstructor().newInstance() }
        )

        val TOOL = PluginType (
            text = "tool",
            instanceOf = ToolPlugin::class.java,
            factory = { impl -> impl.getDeclaredConstructor().newInstance() }
        )

        val ALL_TYPES = listOf(CHAT, TOOL)
    }
}