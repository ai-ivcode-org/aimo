package org.ivcode.ai.ollama.history

import java.util.UUID

interface OllamaHistoryManagerFactory {
    fun createHistoryManager(id: UUID? = null): OllamaHistoryManager
    fun loadHistoryManager(id: UUID): OllamaHistoryManager?
}