package org.ivcode.aimo.service

import org.ivcode.aimo.model.ChatMessage
import org.ivcode.aimo.model.ChatRequest
import org.springframework.stereotype.Service
import java.io.OutputStream
import java.util.UUID

@Service
class ChatService {
    fun getChatHistory(chatId: UUID): List<ChatMessage> {
        TODO("not implemented")
    }

    fun chat (
        chatId: UUID,
        chatRequest: ChatRequest,
        output: OutputStream
    ) {
        TODO("not implemented")
    }
}