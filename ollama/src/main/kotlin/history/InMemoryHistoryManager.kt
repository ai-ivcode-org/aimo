package org.ivcode.ai.ollama.history

import java.util.*

class InMemoryHistoryManager (
    override val id: UUID = UUID.randomUUID(),
) : OllamaHistoryManager {
    private val history = mutableListOf<ChatMessage>()

    override fun getMessages(): List<ChatMessage> {
        return history.toList()
    }

    override fun addMessages(messages: List<ChatMessage>) {
        history.addAll(messages)
    }
}