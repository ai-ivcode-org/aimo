package org.ivcode.ai.plugin.api.chat.filter

import org.ivcode.ai.plugin.api.chat.language.ToolInfo
import java.util.UUID

interface ToolFilter {
    fun apply(request: ToolRequest, next: ToolFilterChain): ToolResponse
}

class ToolRequest (
    val chatId: UUID,
    val controller: Any,
    val toolInfo: ToolInfo,
    val params: Map<String, Any?>,
)

data class ToolResponse (
    val message: String
)

class ToolFilterChain (
    private val filter: ToolFilter?,
    private val chain: ToolFilterChain?,
) {
    fun proceed(request: ToolRequest): ToolResponse {
        return if (filter != null && chain != null) {
            filter.apply(request, chain)
        } else {
            throw IllegalStateException("No filter or chain to proceed with")
        }
    }
}