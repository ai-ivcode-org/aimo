package org.ivcode.ai.ollama.language

import org.ivcode.ai.ollama.client.Options
import org.ivcode.ai.ollama.client.Tool as OllamaTool
import org.ivcode.ai.ollama.client.ChatRequest as OllamaChatRequest
import org.ivcode.ai.ollama.client.ToolCall as OllamaToolCall
import org.ivcode.ai.ollama.client.ToolCallFunction as OllamaToolCallFunction
import org.ivcode.ai.plugin.api.chat.language.ChatMessageRole
import org.ivcode.ai.plugin.api.chat.language.ChatRequest
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.time.Duration
import org.ivcode.ai.plugin.api.chat.language.ChatToolCall as AimoChatToolCall
import org.ivcode.ai.ollama.client.Message as OllamaMessage
import org.ivcode.ai.plugin.api.chat.language.ChatMessage as AimoChatMessage

private val logger: Logger = LoggerFactory.getLogger("OllamaLanguageModelClient")

internal fun createOllamaChatRequest(
    model: String,
    chatRequest: ChatRequest,
    toolRegistry: ToolRegistry,
    keepAlive: Duration? = null,
    logProb: Boolean? = null,
    topLogProbs: Int? = null,
    options: Options? = null
) = OllamaChatRequest (
    model = model,
    messages = chatRequest.systemMessages.toOllamaSystemMessages()
            + chatRequest.history.toOllamaMessages()
            + OllamaMessage(role = "user", content = chatRequest.prompt),
    stream = chatRequest.stream,
    keepAlive = keepAlive,
    tools = chatRequest.toOllamaTools(toolRegistry),
    logProbs = logProb,
    topLogProbs = topLogProbs,
    options = options
)

private fun ChatRequest.toOllamaTools(toolRegistry: ToolRegistry): List<OllamaTool> {
    return this.tools.mapNotNull { name -> toolRegistry.getTool(name)?.ollamaTool ?: run {
        logger.warn("Tool of name $name is not registered in the tool registry, skipping")
        null
    }}
}

private fun List<String>.toOllamaSystemMessages() = this.map { OllamaMessage(role = "system", content = it) }
private fun List<AimoChatMessage>.toOllamaMessages() = this.map { it.toOllamaMessage() }

private fun AimoChatMessage.toOllamaMessage()  = OllamaMessage (
    role = this.role.toOllamaRole(),
    content = this.message ?: "",
    thinking = this.thinking,
    toolCalls = this.toolCalls?.map { it.toOllamaToolCall() },
)

private fun ChatMessageRole.toOllamaRole() = when (this) {
    ChatMessageRole.USER -> "user"
    ChatMessageRole.ASSISTANT -> "assistant"
    ChatMessageRole.TOOL -> "tool"
}

private fun AimoChatToolCall.toOllamaToolCall(): OllamaToolCall {
    val function = OllamaToolCallFunction(
        name = this.toolName,
        arguments = this.arguments
    )

    return OllamaToolCall (
        function = function,
    )
}
