package org.ivcode.ai.ollama.language

import org.ivcode.ai.ollama.client.ToolCallFunction
import org.ivcode.ai.plugin.api.chat.language.ChatMessage
import org.ivcode.ai.plugin.api.chat.language.ChatMessageRole
import org.ivcode.ai.plugin.api.chat.language.ChatToolCall as AimoToolCall
import org.ivcode.ai.ollama.client.Message as OllamaMessage
import org.ivcode.ai.plugin.api.chat.language.ChatResponse as AimoChatResponse
import org.ivcode.ai.ollama.client.ChatResponse as OllamaChatResponse

internal fun OllamaChatResponse.toAimoChatResponse(): AimoChatResponse = AimoChatResponse (
    message = this.message.toAimoMessage(),
    timestamp = this.createdAt,
)

internal fun OllamaMessage.toAimoMessage(): ChatMessage = ChatMessage (
    role = this.role.toAimoRole(),
    message = this.content,
    thinking = this.thinking,
    toolCalls = this.toolCalls?.map { it.function.toAimoToolCall() },
    toolName = this.toolName,
)

internal fun String.toAimoRole() = when (this.lowercase()) {
    "user" -> ChatMessageRole.USER
    "tool" -> ChatMessageRole.TOOL
    "assistant" -> ChatMessageRole.ASSISTANT
    else -> {
        throw IllegalArgumentException("Invalid role: $this")
    }
}

internal fun ToolCallFunction.toAimoToolCall() = AimoToolCall(
    toolName = this.name,
    arguments = this.arguments
)