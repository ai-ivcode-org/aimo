package org.ivcode.ai.ollama.agent

import org.ivcode.ai.ollama.history.OllamaChatSessionInfo
import java.util.UUID

interface OllamaChatAgentFactory {
    fun getChatSessionInfos(): List<OllamaChatSessionInfo>
    fun createOllamaSession(): OllamaChatAgent
    fun createOllamaSession(historyId: UUID): OllamaChatAgent?
}