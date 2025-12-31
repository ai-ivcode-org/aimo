package org.ivcode.ai.ollama.history

import java.util.UUID

interface OllamaHistoryManager {
    val id: UUID
    var title: String?
    fun getMessages(): List<ChatMessage>
    fun addMessages(messages: List<ChatMessage>)
}