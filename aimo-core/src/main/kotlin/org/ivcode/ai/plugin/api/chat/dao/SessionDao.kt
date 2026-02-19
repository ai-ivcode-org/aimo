package org.ivcode.ai.plugin.api.chat.dao

import org.ivcode.ai.plugin.api.chat.language.ChatResponse
import org.ivcode.ai.plugin.api.chat.session.ChatSessionResponse
import java.util.UUID

interface SessionDao {
    // TODO. We need a way to define an attribute (example: user id)
    fun createSession(): UUID
    // TODO. We need a way to require an attribute (example: user id)
    fun getSessions(): List<UUID>
    fun deleteSession(chatId: UUID): Boolean
    fun getResponses(chatId: UUID): List<ChatSessionResponse>
    fun addResponse(chatId: UUID, response: ChatSessionResponse)
    fun deleteResponse(chatId: UUID, id: Int)
    fun getAttribute(chatId: UUID, name: String): Any?
    fun setAttribute(chatId: UUID, name: String, value: Any?)
    fun deleteAttribute(chatId: UUID, name: String)
}
