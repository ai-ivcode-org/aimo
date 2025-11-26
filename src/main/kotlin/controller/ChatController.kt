package org.ivcode.beeboop.controller

import org.ivcode.beeboop.model.ChatRequest
import org.ivcode.beeboop.service.ChatService
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody

@RestController("/chat")
class ChatController(
    private val chatService: ChatService,
) {

    @PostMapping("/{chatId}", produces = [MediaType.APPLICATION_JSON_VALUE, "application/x-ndjson"])
    fun chat (
        @PathVariable chatId: String,
        @RequestBody request: ChatRequest
    ): ResponseEntity<StreamingResponseBody> {
        val contentType = if(request.stream) {
            "application/x-ndjson"
        } else {
            MediaType.APPLICATION_JSON_VALUE
        }

        val body = StreamingResponseBody { output ->
            TODO("not implemented")
        }

        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(contentType))
            .body(body)
    }

    @GetMapping("/history/{chatId}")
    fun getHistory(
        @PathVariable chatId: String
    ) {

    }
}