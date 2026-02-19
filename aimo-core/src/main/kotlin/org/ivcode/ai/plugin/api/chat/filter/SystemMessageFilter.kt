package org.ivcode.ai.plugin.api.chat.filter

import java.util.UUID

interface SystemMessageFilter {
    fun apply(request: SystemMessageRequest, next: SystemMessageFilterChain): SystemMessageResponse
}

class SystemMessageRequest (
    val chatId: UUID,
    val controller: Any,
)

data class SystemMessageResponse (
    val message: String?
)

class SystemMessageFilterChain(
    private val filter: SystemMessageFilter?,
    private val next: SystemMessageFilterChain?
) {
    fun proceed(request: SystemMessageRequest): SystemMessageResponse {
        return if (filter != null && next != null) {
            filter.apply(request, next)
        } else {
            throw IllegalStateException("No filter or chain to proceed with")
        }
    }
}
