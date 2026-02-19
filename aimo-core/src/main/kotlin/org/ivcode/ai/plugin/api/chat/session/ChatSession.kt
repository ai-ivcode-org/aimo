package org.ivcode.ai.plugin.api.chat.session

import org.ivcode.ai.plugin.api.chat.Aimo
import org.ivcode.ai.plugin.api.chat.language.LanguageModelClient
import org.ivcode.ai.plugin.api.chat.dao.SessionDao
import org.ivcode.ai.plugin.api.chat.language.ChatMessage
import org.ivcode.ai.plugin.api.chat.language.ChatRequest
import org.ivcode.ai.plugin.api.chat.language.ChatResponse
import java.util.UUID

interface ChatSession {
    val chatId: UUID
    fun chat (request: ChatSessionRequest, callback: ChatSessionCallback): ChatSessionResponse
}

internal class ChatSessionImpl (
    override val chatId: UUID,
    languageModel: String?,
    aimo: Aimo,
): ChatSession {

    private val systemMessages: List<ChatSessionSystemMessage> = aimo.getChatSessionSystemMessage()
    private val tools: List<ChatSessionTool> = aimo.getChatSessionTools()
    private val dao: SessionDao = aimo.getDataAccessObject()
    private val languageModelClient: LanguageModelClient = aimo.getLanguageModel(languageModel).connect()

    override fun chat (request: ChatSessionRequest, callback: ChatSessionCallback): ChatSessionResponse {
        val responses = dao.getResponses(chatId).sortedBy { it.id }
        val requestId = responses.lastOrNull()?.id ?: 1

        val history = responses.toHistory()

        var lastResponse: ChatResponse? = null
        val messageList = mutableListOf<ChatMessage>()

        while (lastResponse==null || (lastResponse.message.toolCalls?.isNotEmpty() ?: false)) {
            lastResponse?.message?.toolCalls?.forEach { toolCall ->
                // TODO execute tools
                // TODO add result to messageList
            }

            val lmRequest = ChatRequest(
                prompt = request.prompt,
                stream = request.stream,
                systemMessages = systemMessages.mapNotNull { systemMessage -> systemMessage(chatId) },
                history = history + messageList,
                tools = tools.map { it.info.name },
            )

            val messageId = messageList.size + 1
            lastResponse = languageModelClient.chat(lmRequest) { lmStreamResponse ->
                callback(lmStreamResponse.toChatSessionResponse(requestId, messageId))
            }
            messageList.add(lastResponse.message)
        }

        // TODO convert lastResponse and messages to response
    }

    private fun List<ChatSessionResponse>.toHistory(): List<ChatMessage> {
        val history = mutableListOf<ChatMessage>()
        this.forEach { chatSessionResponse ->
            history.addAll(chatSessionResponse.toHistory())
        }

        return history
    }

    private fun ChatSessionResponse.toHistory() = this.messages.sortedBy { it.id }.map { it.toChatMessage() }

    private fun ChatSessionMessage.toChatMessage() = ChatMessage(
        role = this.role,
        message = this.message,
        thinking = this.thinking,
        toolCalls = this.toolCalls,
        toolName = this.toolName,
    )

    private fun ChatResponse.toChatSessionResponse(requestId: Int, messageId: Int) = ChatSessionResponse(
        id = requestId,
        messages = listOf(this.message.toChatSessionMessage(messageId)),
        timestamp = this.timestamp
    )

    private fun ChatMessage.toChatSessionMessage(id: Int) = ChatSessionMessage (
        id = id,
        role = this.role,
        message = this.message,
        thinking = this.thinking,
        toolCalls = this.toolCalls,
        toolName = this.toolName,
    )
}