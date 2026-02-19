package org.ivcode.aimo.plugin.impl.chat.context

import org.ivcode.aimo.model.ChatSession
import java.util.UUID

interface ChatSessionManager {
    fun getSessionInfo(): List<ChatSession>
    fun createSession(): StatefulChatSession
    fun getSession(chatId: UUID): StatefulChatSession?
    fun deleteSession(chatId: UUID): Boolean
}
