package org.ivcode.ai.ollama.language

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.ivcode.ai.ollama.client.OllamaChatClient
import org.ivcode.ai.plugin.api.chat.language.LanguageModel
import org.ivcode.ai.plugin.api.chat.language.LanguageModelClient
import org.ivcode.ai.plugin.api.chat.language.ToolInfo as AimoToolInfo

class OllamaLanguageModel (
    override val name: String = "ollama",
    val url: String,
    val model: String,
    val mapper: ObjectMapper = jacksonObjectMapper()
) : LanguageModel {

    private val toolRegistry = ToolRegistry()

    override fun registerTool(tool: AimoToolInfo): Unit = toolRegistry.registerTool(tool)

    override fun connect(): LanguageModelClient {
        val chatClient = OllamaChatClient(url, mapper)

        return OllamaLanguageModelClient (
            model = this.model,
            chatClient = chatClient,
            toolRegistry = toolRegistry,
        )
    }
}
