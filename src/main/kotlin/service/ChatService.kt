package org.ivcode.beeboop.service

import org.ivcode.beeboop.model.ChatMessage
import org.ivcode.beeboop.model.ChatRequest
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