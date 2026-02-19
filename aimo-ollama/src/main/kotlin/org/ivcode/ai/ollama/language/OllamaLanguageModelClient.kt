package org.ivcode.ai.ollama.language

import org.ivcode.ai.ollama.client.OllamaChatClient
import org.ivcode.ai.plugin.api.chat.language.ChatCallback as AimoChatCallback
import org.ivcode.ai.plugin.api.chat.language.ChatRequest as AimoChatRequest
import org.ivcode.ai.plugin.api.chat.language.ChatResponse as AimoChatResponse
import org.ivcode.ai.plugin.api.chat.language.LanguageModelClient

private val LOGGER = org.slf4j.LoggerFactory.getLogger(OllamaLanguageModelClient::class.java)

internal class OllamaLanguageModelClient (
    val model: String,
    val chatClient: OllamaChatClient,
    val toolRegistry: ToolRegistry
): LanguageModelClient {

    override fun chat(
        request: AimoChatRequest,
        callback: AimoChatCallback?
    ): AimoChatResponse {
        val request = createOllamaChatRequest(model, request, toolRegistry)

        val response = chatClient.chat(request) { stream ->
            callback?.invoke(stream.toAimoChatResponse())
        }

        return response.toAimoChatResponse()
    }
}
