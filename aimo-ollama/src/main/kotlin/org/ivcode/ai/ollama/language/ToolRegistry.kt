package org.ivcode.ai.ollama.language

import org.ivcode.ai.plugin.api.chat.language.ToolInfo as AimoToolInfo

internal class ToolRegistry {
    private val tools = mutableMapOf<String, OllamaAimoTool>()

    fun registerTool(tool: AimoToolInfo) {
        val name = tool.name
        val ollamaTool = tool.toOllamaAimoTool()

        val old = tools.put(name, ollamaTool)
        if (old != null) {
            throw IllegalStateException("Tool of name $name is already registered")
        }
    }

    fun getTool(name: String): OllamaAimoTool? = tools[name]
}