package org.ivcode.ai.ollama.agent

import io.github.ollama4j.Ollama
import io.github.ollama4j.models.chat.OllamaChatResult
import io.github.ollama4j.models.chat.OllamaChatTokenHandler
import io.github.ollama4j.agent.Agent
import io.github.ollama4j.models.chat.OllamaChatMessage
import org.ivcode.ai.ollama.history.ChatMessage
import org.ivcode.ai.ollama.system.OllamaSystemMessage
import java.util.UUID

/**
 * Agent interface for chatting with Ollama models. This is similar to Ollama4j's [Agent] interface but
 * add hooks for customization. It allows for plugin support in defining dynamic system messages, history
 * management,and tooling.
 */
interface OllamaChatAgent {
    val ollama: Ollama
    val model: String
    val systemMessages: MutableList<OllamaSystemMessage>
    fun chat(message: String, tokenHandler: OllamaChatTokenHandler? = null): OllamaChatResult

    fun getSessionId(): UUID
    fun getSessionTitle(): String?
    fun setSessionTitle(title: String)
    fun getChatHistory(): List<ChatMessage>
}