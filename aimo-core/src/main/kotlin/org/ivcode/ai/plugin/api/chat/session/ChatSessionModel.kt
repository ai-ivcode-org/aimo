package org.ivcode.ai.plugin.api.chat.session

import org.ivcode.ai.plugin.api.chat.language.ChatMessageRole
import org.ivcode.ai.plugin.api.chat.language.ChatToolCall
import org.ivcode.ai.plugin.api.chat.language.ToolInfo
import java.time.Instant
import java.util.UUID

typealias ChatSessionToolExecutor = (chatId: UUID, tool: ToolInfo, arguments: Map<String, Any?>) -> String

typealias ChatSessionCallback = (ChatSessionResponse) -> Unit

data class ChatSessionTool (
    val info: ToolInfo,
    val executor: ChatSessionToolExecutor,
)

typealias ChatSessionSystemMessage = (chatId: UUID) -> String?

data class ChatSessionResponse (
    val id: Int,
    val messages: List<ChatSessionMessage>,
    val timestamp: Instant?,
)

data class ChatSessionMessage (
    val id: Int,
    val role: ChatMessageRole,
    val message: String? = null,
    val thinking: String? = null,
    val toolCalls: List<ChatToolCall>? = null,
    val toolName: String? = null,
)

data class ChatSessionRequest (
    val prompt: String,
    val stream: Boolean = false,
)
