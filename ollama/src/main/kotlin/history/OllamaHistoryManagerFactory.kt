package org.ivcode.ai.ollama.history

import java.util.UUID

interface OllamaHistoryManagerFactory {
    fun getChatSessionInfos(): List<OllamaChatSessionInfo>
    fun deleteChatSession(id: UUID): Boolean
    fun createHistoryManager(id: UUID? = null): OllamaHistoryManager
    fun loadHistoryManager(id: UUID): OllamaHistoryManager?
}