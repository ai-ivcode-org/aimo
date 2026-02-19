package org.ivcode.aimo.plugin.impl.chat.context

import org.ivcode.ai.plugin.api.chat.context.ChatContext
import org.ivcode.ai.plugin.api.chat.context.ChatContextGroup
import org.ivcode.ai.plugin.api.chat.language.ChatCallback
import org.ivcode.ai.plugin.api.chat.language.ChatMessage
import org.ivcode.ai.plugin.api.chat.language.ChatRequest
import org.ivcode.ai.plugin.api.chat.language.ChatResult
import org.ivcode.ai.plugin.api.chat.language.ChatRequestState
import org.ivcode.ai.plugin.api.chat.language.LanguageModelClient
import org.ivcode.ai.plugin.api.chat.language.SystemMessage
import org.ivcode.ai.plugin.api.chat.language.Tool
import java.util.UUID

class StatefulChatSessionImpl (
    private val chatId: UUID,
    private val lang: LanguageModelClient,
    private val systemMessages: List<SystemMessage>,
    private val tools: List<Tool>
): StatefulChatSession {

    private val contextGroup = ChatContextGroup()
    private val contextualSystemMessages = systemMessages.map { contextGroup.withContext (it) }
    private val contextualTools = tools.map { contextGroup.withContext(it) }

    private var title: String? = null
    private val history: MutableList<ChatMessage> = mutableListOf()

    override fun getSystemMessages(): List<SystemMessage> {
        // return the non-contextual messages
        return systemMessages
    }

    override fun getTools(): List<Tool> {
        // return the non-contextual tools
        return tools
    }

    override fun getChatId(): UUID {
        return chatId
    }

    override fun getTitle(): String? {
        return title
    }

    override fun setTitle(title: String) {
        this.title = title
    }

    override fun getHistory(): List<ChatMessage> {
        return history
    }

    override fun chat (
        request: ChatRequest,
        callback: ChatCallback?
    ): ChatResult {
        setContext()
        val state = createState()
        val result =  lang.chat(request, state, callback)

        history.add(result.message)
        return result
    }

    private fun setContext() {
        contextGroup.context = ChatContext (
            session = this
        )
    }

    private fun createState() = ChatRequestState (
        systemMessages = contextualSystemMessages,
        tools = contextualTools,
        history = getHistory()
    )
}