package org.ivcode.aimo.model

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
