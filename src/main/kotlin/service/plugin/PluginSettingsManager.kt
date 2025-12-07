package org.ivcode.beeboop.service.plugin

import org.ivcode.beeboop.plugin.persistence.Datastore

private const val CHAT_CONFIG_KEY = "chat-plugins"
private const val TOOL_CONFIG_KEY = "tool-plugins"

class PluginSettingsManager (
    private val pluginConfigDatastore: Datastore<PluginConfiguration>
) {

    fun getChatSettings(pluginId: String): PluginConfiguration.Plugin? {
        val chatConfig = pluginConfigDatastore.get(CHAT_CONFIG_KEY)
              ?: throw IllegalStateException("Chat plugin configuration not found")

        return chatConfig.plugins[pluginId]
    }

    fun getToolSettings(pluginId: String): PluginConfiguration.Plugin? {
        val toolConfig = pluginConfigDatastore.get(TOOL_CONFIG_KEY)
              ?: throw IllegalStateException("Tool plugin configuration not found")

        return toolConfig.plugins[pluginId]
    }

    fun getChatSettings(): PluginConfiguration {
        return pluginConfigDatastore.get(CHAT_CONFIG_KEY)
              ?: throw IllegalStateException("Chat plugin configuration not found")
    }

    fun getToolSettings(): PluginConfiguration {
        return pluginConfigDatastore.get(TOOL_CONFIG_KEY)
              ?: throw IllegalStateException("Tool plugin configuration not found")
    }

    fun saveChatSettings(config: PluginConfiguration) {
        // TODO validate settings against plugin schema

        pluginConfigDatastore.put(CHAT_CONFIG_KEY, config)
    }

    fun saveToolSettings(config: PluginConfiguration) {
        // TODO validate settings against plugin schema

        pluginConfigDatastore.put(TOOL_CONFIG_KEY, config)
    }

    fun saveChatSettings(pluginId: String, chat: PluginConfiguration.Plugin) {
        val chatConfig = getChatSettings()

        // TODO validate settings against plugin schema


        val updatedPlugins = chatConfig.plugins.toMutableMap()
        updatedPlugins[pluginId] = chat
        val updatedConfig = PluginConfiguration(updatedPlugins)

        pluginConfigDatastore.put(CHAT_CONFIG_KEY, updatedConfig)
    }

    fun saveToolSettings(pluginId: String, tool: PluginConfiguration.Plugin) {
        val toolConfig = getToolSettings()

        // TODO validate settings against plugin schema

        val updatedPlugins = toolConfig.plugins.toMutableMap()
        updatedPlugins[pluginId] = tool
        val updatedConfig = PluginConfiguration(updatedPlugins)

        pluginConfigDatastore.put(TOOL_CONFIG_KEY, updatedConfig)
    }
}