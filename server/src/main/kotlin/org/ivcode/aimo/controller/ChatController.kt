package org.ivcode.aimo.controller

import org.ivcode.aimo.model.ChatRequest
import org.ivcode.aimo.model.NewChatResponse
import org.ivcode.aimo.service.ChatService
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody
import java.util.UUID

@RestController
@RequestMapping("/chat")
class ChatController(
    private val chatService: ChatService,
) {

    @PostMapping("/{chatId}", produces = [MediaType.APPLICATION_JSON_VALUE, "application/x-ndjson"])
    fun chat (
        @PathVariable chatId: UUID,
        @RequestBody request: ChatRequest
    ): ResponseEntity<StreamingResponseBody> {

        if(!chatService.sessionExists(chatId)) {
            return ResponseEntity.notFound().build()
        }

        val contentType = if(request.stream) {
            "application/x-ndjson"
        } else {
            MediaType.APPLICATION_JSON_VALUE
        }

        val body = StreamingResponseBody { output ->
            output.use {
                chatService.chat (
                    chatId = chatId,
                    chatRequest = request,
                    output = output
                )
            }
        }

        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(contentType))
            .body(body)
    }

    @PostMapping("/new")
    fun createChat (
    ): NewChatResponse {
        return NewChatResponse(
            chatId = chatService.createChat().toString()
        )
    }

    @GetMapping("/history/{chatId}")
    fun getHistory(
        @PathVariable chatId: UUID
    ) {

    }
}