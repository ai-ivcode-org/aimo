package org.ivcode.beeboop.service.plugin


class PluginService (
    private val pluginContextManager: PluginContextManager,
    private val pluginSettingsManager: PluginSettingsManager
) {
    fun loadChatContext(chatId: String) =
        pluginContextManager.loadChatContext(chatId)

    fun getChatSettings(pluginId: String) =
        pluginSettingsManager.getChatSettings(pluginId)

    fun getToolSettings(pluginId: String) =
        pluginSettingsManager.getToolSettings(pluginId)

    fun getChatSettings(): PluginConfiguration =
        pluginSettingsManager.getChatSettings()

    fun getToolSettings(): PluginConfiguration =
        pluginSettingsManager.getToolSettings()

    fun saveChatSettings(config: PluginConfiguration) =
        pluginSettingsManager.saveChatSettings(config)

    fun saveToolSettings(config: PluginConfiguration) =
        pluginSettingsManager.saveToolSettings(config)

    fun saveChatSettings(pluginId: String, chat: PluginConfiguration.Plugin) =
        pluginSettingsManager.saveChatSettings(pluginId, chat)

    fun saveToolSettings(pluginId: String, tool: PluginConfiguration.Plugin) =
        pluginSettingsManager.saveToolSettings(pluginId, tool)
}