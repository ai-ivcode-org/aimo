package org.ivcode.ai.ollama.history

import java.util.UUID

interface OllamaHistoryManager {
    val id: UUID
    fun getMessages(): List<ChatMessage>
    fun addMessages(messages: List<ChatMessage>)
}