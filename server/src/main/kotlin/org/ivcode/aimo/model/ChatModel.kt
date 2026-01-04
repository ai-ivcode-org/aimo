package org.ivcode.aimo.model

import java.util.UUID

data class NewChatResponse (
    val chatId: String
)

data class ChatRequest (
    val message: String,
    val stream: Boolean = false
)

data class ChatMessage (
    val id: Long,
    val role: Role,
    val response: String?,
    val thinking: String?,
    val timestamp: Long?,
    val done: Boolean
) {
    enum class Role {
        USER,
        ASSISTANT,
        SYSTEM,
        TOOL
    }
}

data class ChatSession (
    val chatId: UUID,
    val title: String?
)

data class ChatSessionUpdateRequest (
    val title: String
)