package org.ivcode.beeboop.service.plugin

import org.ivcode.beeboop.datastore.storage.Storage
import org.ivcode.beeboop.plugin.chat.ChatPlugin
import org.ivcode.beeboop.plugin.tool.ToolPlugin
import org.ivcode.beeboop.service.plugin.config.BEAN_PLUGIN_PACKAGE_STORE
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component

@Component
class PluginLoader(
    private val chatPlugins: List<ChatPlugin>,
    private val toolPlugins: List<ToolPlugin>,
    @param:Qualifier(BEAN_PLUGIN_PACKAGE_STORE) private val pluginPackageStorage: Storage,
) {
    private var availableChatPlugins = mutableMapOf<String, ChatPlugin>().apply {
        for (plugin in chatPlugins) {
            this[plugin.name] = plugin
        }
    }
    private var availableToolPlugins = mutableMapOf<String, ToolPlugin>().apply {
        for (plugin in toolPlugins) {
            this[plugin.name] = plugin
        }
    }

    @Synchronized
    fun getChatPlugin(pluginId: String): ChatPlugin {
        val chatPlugin = availableChatPlugins[pluginId]
        if (chatPlugin == null) {
            // TODO attempt to load from package datastore
            throw IllegalArgumentException("Chat plugin with ID '$pluginId' not found")
        } else {
            return chatPlugin
        }
    }

    @Synchronized
    fun getToolPlugin(pluginId: String): ToolPlugin {
        val toolPlugin = availableToolPlugins[pluginId]
        if (toolPlugin == null) {
            // TODO attempt to load from package datastore
            throw IllegalArgumentException("Tool plugin with ID '$pluginId' not found")
        } else {
            return toolPlugin
        }
    }


}


