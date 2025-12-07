package org.ivcode.beeboop.service.plugin

import org.ivcode.beeboop.datastore.session.Session
import org.ivcode.beeboop.plugin.chat.ChatModel
import org.ivcode.beeboop.plugin.chat.ChatPlugin
import org.ivcode.beeboop.plugin.tool.ToolPlugin
import org.ivcode.beeboop.plugin.tool.ToolPluginModel

class PluginContextManager (
    private val pluginLoader: PluginLoader,
    private val chatConfigSession: Session<ChatConfig>,
    private val pluginSettingsManager: PluginSettingsManager
) {
    fun loadChatContext(chatId: String): ChatContext {
        // First, attempt to load the chat configuration from the cache
        val cachedContext = loadFromCache(chatId)
        if (cachedContext != null) {
            return cachedContext
        }

        // If not found in cache, load from the permanent configuration store
        return loadFromConfig(chatId)
    }

    /**
     * Load a ChatContext based on the provided ChatConfig.
     *
     * This is to load in a ChatContext for an active session. The idea is to pull the configuration from the current
     * session using a fast cache rather than pulling the configuration from a slower permanent store.
     *
     * @param config the ChatConfig containing plugin ID and parameters.
     * @return a fully constructed ChatContext.
     */
    private fun loadFromCache(chatId: String): ChatContext? {
        val config = chatConfigSession.get(chatId) ?: return null

        val chatModel = getChatModel(config)
        val toolModels = getToolPluginModels(config)

        return ChatContext(
            model = chatModel,
            staticSystemMessages = toolModels.flatMap { it.staticSystemMessages },
            dynamicSystemMessages = toolModels.flatMap { it.dynamicSystemMessages },
            tools = toolModels.flatMap { it.tools }
        )
    }

    private fun getChatModel(config: ChatConfig): ChatModel {
        val chatPlugin = pluginLoader.getChatPlugin(config.chat.pluginId)
        return chatPlugin.createChatModel(config.chat.settings)
    }

    private fun getToolPluginModels(config: ChatConfig): List<ToolPluginModel> {
        val toolModels = mutableListOf<ToolPluginModel>()

        for (toolConfig in config.tools) {
            val toolPlugin = pluginLoader.getToolPlugin(toolConfig.pluginId)
            val toolModel = toolPlugin.createToolModel(toolConfig.settings)
            toolModels.add(toolModel)
        }

        return toolModels
    }

    private fun loadFromConfig(chatId: String): ChatContext {
        //  --== Pull the Chat Model ==--
        val chatPluginConfig = pluginSettingsManager.getChatSettings()
        // find the enabled chat plugin
        val chatPlugin = getEnabledChatPlugin(chatPluginConfig)
        // get the saved settings for the enabled chat plugin
        val settings = chatPluginConfig.plugins[chatPlugin.id] ?: throw IllegalStateException("No settings found for enabled chat plugin '${chatPlugin.id}'")
        // create the chat model
        val chatModel = chatPlugin.createChatModel(settings.settings)



        // --== Pull the Tool Models ==--
        val toolPluginConfig = pluginSettingsManager.getToolSettings()
        // find the enabled tool plugins
        val toolPlugins = getEnabledToolPlugins(toolPluginConfig)

        // hold onto the tool settings to cache
        val toolSettings = mutableListOf<SessionPluginConfig>()

        // a collection of tool models
        val toolModels = mutableListOf<ToolPluginModel>()

        for (toolPlugin in toolPlugins) {
            val settings = toolPluginConfig.plugins[toolPlugin.id] ?: throw IllegalStateException("No settings found for enabled tool plugin '${toolPlugin.id}'")
            toolPlugin.createToolModel(settings.settings)

            toolSettings.add(
                SessionPluginConfig(
                    pluginId = toolPlugin.id,
                    settings = settings.settings
                )
            )

            toolModels.add(toolPlugin.createToolModel(settings.settings))
        }

        // --== Cache the Chat Configuration ==--
        chatConfigSession.put(chatId, ChatConfig(
            chat = SessionPluginConfig(
                pluginId = chatPlugin.id,
                settings = settings.settings
            ),
            tools = toolSettings
        ))

        // --== Build and Return the Chat Context ==--
        return ChatContext(
            model = chatModel,
            staticSystemMessages = toolModels.flatMap { it.staticSystemMessages },
            dynamicSystemMessages = toolModels.flatMap { it.dynamicSystemMessages },
            tools = toolModels.flatMap { it.tools }
        )
    }

    fun getEnabledChatPlugin(config: PluginConfiguration): ChatPlugin {
        val enabledPlugins = config.plugins.filter { it.value.enabled }
        if (enabledPlugins.size != 1) {
            throw IllegalStateException("Expected exactly one enabled chat plugin, found ${'$'}{enabledPlugins.size}")
        }

        val pluginId = enabledPlugins.keys.first()
        val chatPlugin = pluginLoader.getChatPlugin(pluginId)

        return chatPlugin
    }

    fun getEnabledToolPlugins(config: PluginConfiguration): List<ToolPlugin> {
        val enabledPlugins = config.plugins.filter { it.value.enabled }
        val toolPluginList = mutableListOf<ToolPlugin>()

        for (pluginConfig in enabledPlugins.entries) {
            val pluginId = pluginConfig.key
            val toolPlugin = pluginLoader.getToolPlugin(pluginId)
            toolPluginList.add(toolPlugin)
        }

        return toolPluginList
    }
}