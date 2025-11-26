package org.ivcode.beeboop.model

data class ChatRequest (
    val message: String,
    val stream: Boolean = false
)

data class ChatMessage (
    val id: Long,
    val response: String,
    val thinking: String,
    val timestamp: Long,
    val done: Boolean
)