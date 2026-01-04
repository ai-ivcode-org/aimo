package org.ivcode.aimo.controller

import org.ivcode.aimo.model.ChatSession
import org.ivcode.aimo.model.ChatSessionUpdateRequest
import org.ivcode.aimo.model.NewChatResponse
import org.ivcode.aimo.service.ChatService
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/chat-session")
class ChatSessionController(
    private val chatService: ChatService,
) {
    @PostMapping("/")
    fun createChatSession (
    ): NewChatResponse {
        return NewChatResponse(
            chatId = chatService.createChat().toString()
        )
    }

    @GetMapping("/")
    fun getChatSessions (): List<ChatSession> = chatService.getSessions()

    @PostMapping("/{chatId}", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun updateChatSession(
        @PathVariable chatId: UUID,
        @RequestBody request: ChatSessionUpdateRequest
    ) {
        chatService.updateChatSession(chatId, request.title)
    }

    @DeleteMapping("/{chatId}")
    fun deleteChatSession (
        @PathVariable chatId: UUID
    ) {
        chatService.deleteSession(chatId)
    }
}