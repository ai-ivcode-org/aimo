package org.ivcode.ai.ollama.agent

import java.util.UUID

interface OllamaChatAgentFactory {
    fun createOllamaSession(): OllamaChatAgent
    fun createOllamaSession(historyId: UUID): OllamaChatAgent?
}