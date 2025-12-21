package org.ivcode.ai.ollama.agent

import io.github.ollama4j.Ollama
import org.ivcode.ai.ollama.core.OllamaFactory
import org.ivcode.ai.ollama.history.OllamaHistoryManagerFactory
import org.ivcode.ai.ollama.system.OllamaSystemMessageFactory
import java.util.UUID

class Ollama4jChatAgentFactory (
    private val model: String,
    ollamaFactory: OllamaFactory,
    private val historyManagerFactory: OllamaHistoryManagerFactory,
    private val systemMessageFactory: OllamaSystemMessageFactory
): OllamaChatAgentFactory {

    private val ollama: Ollama = ollamaFactory.createOllama()

    override fun createOllamaSession(): OllamaChatAgent {
        val historyManager = historyManagerFactory.createHistoryManager()
        val systemMessages = systemMessageFactory.createSystemMessages()

        return Ollama4jChatAgent(
            model = model,
            ollama = ollama,
            historyManager = historyManager,
            systemMessages = systemMessages,
        )
    }

    override fun createOllamaSession(historyId: UUID): OllamaChatAgent? {
        val historyManager = historyManagerFactory.loadHistoryManager(historyId) ?: return null
        val systemMessages = systemMessageFactory.createSystemMessages()

        return Ollama4jChatAgent(
            model = model,
            ollama = ollama,
            historyManager = historyManager,
            systemMessages = systemMessages,
        )
    }
}