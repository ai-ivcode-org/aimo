package org.ivcode.aimo.service

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.ollama4j.models.chat.OllamaChatMessageRole
import org.ivcode.ai.ollama.agent.OllamaChatAgentFactory
import org.ivcode.aimo.model.ChatMessage
import org.ivcode.aimo.model.ChatRequest
import org.springframework.stereotype.Service
import java.io.OutputStream
import java.util.UUID

@Service
class ChatService(
    private val ollamaSessionFactory: OllamaChatAgentFactory,
    private val objectMapper: ObjectMapper
) {

    fun createChat (): UUID = ollamaSessionFactory.createOllamaSession().getSessionId()

    fun chat (
        chatId: UUID,
        chatRequest: ChatRequest,
        output: OutputStream
    ) {
        val session = ollamaSessionFactory.createOllamaSession(chatId) ?: throw IllegalStateException("Failed to create Ollama chat session")
        val size = session.getChatHistory().size
        session.chat(chatRequest.message) { resp ->
            val chatMessage = ChatMessage (
                id = size.toLong(),
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

    fun sessionExists (
        chatId: UUID
    ): Boolean {
        val session = ollamaSessionFactory.createOllamaSession(chatId)
        return session != null
    }

    fun history (
        chatId: UUID
    ): List<ChatMessage> {
        val session = ollamaSessionFactory.createOllamaSession(chatId) ?: throw IllegalStateException("Failed to create Ollama chat session")

        return session.getChatHistory().mapIndexed { index, msg ->
            ChatMessage (
                id = index.toLong(),
                role = msg.role.toChatMessageRole(),
                response = msg.response,
                thinking = msg.thinking,
                timestamp = msg.timestamp,
                done = true,
            )
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