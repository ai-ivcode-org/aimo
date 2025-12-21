package org.ivcode.ai.ollama.history

import io.github.ollama4j.models.chat.OllamaChatMessageRole
import io.github.ollama4j.models.chat.OllamaChatToolCalls

data class ChatMessage(
    val role: OllamaChatMessageRole,
    val response: String? = null,
    val thinking: String? = null,
    var toolCalls: MutableList<OllamaChatToolCalls>? = null,
    var timestamp: Long? = null
)