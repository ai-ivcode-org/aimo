package org.ivcode.ai.ollama.history

import java.util.UUID

class InMemoryHistoryManagerFactory: OllamaHistoryManagerFactory {
    private val history: MutableMap<UUID, OllamaHistoryManager> = mutableMapOf()

    override fun getChatSessionInfos() = history.values.map {
        OllamaChatSessionInfo (
            id = it.id,
            title = it.title
        )
    }

    override fun createHistoryManager(id: UUID?): OllamaHistoryManager {
        if(id != null) {
            val existing = history[id]
            if(existing != null) {
                return existing
            }
        }

        val manager = InMemoryHistoryManager(id ?: UUID.randomUUID())
        history[manager.id] = manager

        return manager
    }

    override fun loadHistoryManager(id: UUID): OllamaHistoryManager? {
        return history[id]
    }
}
