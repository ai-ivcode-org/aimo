package org.ivcode.aimo.plugin.impl.chat.context

import org.ivcode.ai.plugin.api.chat.language.LanguageModel
import org.ivcode.ai.plugin.api.chat.language.SystemMessage
import org.ivcode.ai.plugin.api.chat.language.Tool
import org.ivcode.aimo.model.ChatSession
import java.util.UUID

class ChatSessionManagerImpl(
    val languageModel: LanguageModel,
    val systemMessages: List<SystemMessage>,
    val tools: List<Tool>
): ChatSessionManager {

    val sessions = mutableMapOf<UUID, StatefulChatSession>()

    override fun createSession(): StatefulChatSession {
        val chatId = UUID.randomUUID()

        val newSession = StatefulChatSessionImpl (
            chatId = chatId,
            lang = languageModel.connect(),
            systemMessages = systemMessages,
            tools = tools,
        )

        sessions[chatId] = newSession
        return newSession
    }

    override fun getSessionInfo(): List<ChatSession> {
        return sessions.values.map { ChatSession (
            chatId = it.getChatId(),
            title = "",
        )}
    }

    override fun getSession(chatId: UUID): StatefulChatSession? {
        return sessions[chatId]
    }

    override fun deleteSession(chatId: UUID): Boolean {
        return sessions.remove(chatId) != null
    }
}