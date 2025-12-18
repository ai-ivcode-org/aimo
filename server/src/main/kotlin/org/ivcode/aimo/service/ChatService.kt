package org.ivcode.aimo.service

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.ollama4j.models.chat.OllamaChatMessageRole
import org.ivcode.ai.ollama.agent.OllamaChatAgentFactory
import org.ivcode.aimo.model.ChatMessage
import org.ivcode.aimo.model.ChatRequest
import org.ivcode.common.data.session.SessionFactory
import org.springframework.stereotype.Service
import java.io.OutputStream
import java.util.UUID

@Service
class ChatService(
    private val ollamaSessionFactory: OllamaChatAgentFactory,
    private val objectMapper: ObjectMapper
) {
    fun chat (
        chatId: UUID,
        chatRequest: ChatRequest,
        output: OutputStream
    ) {
        val session = ollamaSessionFactory.createOllamaSession(chatId) ?: throw IllegalStateException("Failed to create Ollama chat session")
        session.chat(chatRequest.message) { resp ->

            val chatMessage = ChatMessage (
                id = System.currentTimeMillis(),
                role = resp.message.role.toChatMessageRole(),
                response = resp.message.response,
                thinking = resp.message.thinking,
                timestamp = System.currentTimeMillis(),
                done = resp.isDone,
            )

            val json = objectMapper.writeValueAsString(chatMessage)
            output.write((json + "\n").toByteArray())
            output.flush()
        }
    }

    private fun OllamaChatMessageRole.toChatMessageRole(): ChatMessage.Role = when (this.roleName) {
        "system" -> ChatMessage.Role.SYSTEM
        "user" -> ChatMessage.Role.USER
        "assistant" -> ChatMessage.Role.ASSISTANT
        "tool" -> ChatMessage.Role.TOOL
        else -> throw IllegalArgumentException("Unknown role: ${this.roleName}")
    }
}