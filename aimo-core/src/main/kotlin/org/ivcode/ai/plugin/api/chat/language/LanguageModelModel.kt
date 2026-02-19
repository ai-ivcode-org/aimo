package org.ivcode.ai.plugin.api.chat.language

import java.time.Instant

typealias ChatCallback = (ChatResponse) -> Unit

/**
 * Represents a single chat request to the language model.
 *
 * @property prompt the textual prompt or message to send to the model.
 * @property stream when true requests streaming responses from the model (if supported).
 */
data class ChatRequest (
    val prompt: String,
    val stream: Boolean = false,
    val systemMessages: List<String>,
    val history: List<ChatMessage>,
    val tools: List<String>
)

data class ChatResponse (
    val message: ChatMessage,
    var timestamp: Instant? = null
)

data class ChatMessage (
    val role: ChatMessageRole,
    val message: String? = null,
    val thinking: String? = null,
    val toolCalls: List<ChatToolCall>? = null,
    val toolName: String? = null,
)

enum class ChatMessageRole {
    USER,
    ASSISTANT,
    TOOL
}

data class ChatToolCall (
    val toolName: String,
    val arguments: Map<String, Any?>,
)

data class ToolInfo (
    val name: String,
    val description: String?,
    val parameters: List<ToolParameter>,
)

data class ToolParameter (
    val name: String,
    val type: ToolType,
    val description: String?,
    val isNullable: Boolean,
    val isArray: Boolean,
    val enum: List<String>? = null,
)

enum class ToolType {
    BOOLEAN,
    INTEGER,
    FLOAT,
    STRING,
}
