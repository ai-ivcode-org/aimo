package org.ivcode.ai.plugin.api.chat.language

interface LanguageModel {
    val name: String

    fun registerTool(tool: ToolInfo)
    fun connect(): LanguageModelClient
}
