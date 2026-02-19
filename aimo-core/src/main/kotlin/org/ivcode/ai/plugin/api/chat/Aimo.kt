package org.ivcode.ai.plugin.api.chat

import org.ivcode.ai.plugin.api.chat.dao.SessionDao
import org.ivcode.ai.plugin.api.chat.filter.SystemMessageFilter
import org.ivcode.ai.plugin.api.chat.filter.ToolFilter
import org.ivcode.ai.plugin.api.chat.language.LanguageModel
import org.ivcode.ai.plugin.api.chat.session.ChatSession
import org.ivcode.ai.plugin.api.chat.session.ChatSessionSystemMessage
import org.ivcode.ai.plugin.api.chat.session.ChatSessionTool
import java.util.UUID

/**
 * A.I.M.O.: The AI-Middleware-Orchestration Framework
 */
interface Aimo {
    fun getLanguageModel(languageModelName: String? = null): LanguageModel
    fun createChatSession(languageModelName: String? = null): ChatSession
    fun getChatSession(chatId: UUID, languageModelName: String? = null): ChatSession?
    fun getChatSessionTools(): List<ChatSessionTool>
    fun getChatSessionSystemMessage(): List<ChatSessionSystemMessage>
    fun deleteChatSession(chatId: UUID): Boolean
    fun getDataAccessObject(): SessionDao
}


class AimoRegistry: Aimo {
    fun registerController(controller: Any) {}
    fun registerToolFilter(filter: ToolFilter) {}
    fun registerSystemMessageFilter(filter: SystemMessageFilter) {}
    fun registerChatFilter(/*TODO*/) {}
    fun registerSessionFilter(/*TODO*/) {}
    fun registerLanguageModel(languageModelName: String, languageModel: LanguageModel, isPrimary: Boolean = false) {}
    override fun getLanguageModel(languageModelName: String): LanguageModel  { TODO() }
    fun registerDataAccessObject(dao: SessionDao) {}
    fun getDataAccessObject(): SessionDao {TODO()}
    override fun createChatSession(languageModelName: String?): ChatSession { TODO() }
    override fun getChatSession(chatId: UUID, languageModelName: String?): ChatSession? { TODO() }
}

// TODO I need to add attribute lookups. For example, getChatSession with a user attribute should only return a given
// session if the chatId has the given user attribute too