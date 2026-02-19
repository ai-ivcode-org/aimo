package org.ivcode.ai.plugin.api.chat.filter

import org.ivcode.ai.plugin.api.chat.session.ChatSessionSystemMessage
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.util.UUID

internal fun createSystemMessage (
    controller: Any,
    filters: List<SystemMessageFilter>,
    field: Field
): ChatSessionSystemMessage {
    var chain = SystemMessageFilterChain(FieldInvokerSystemMessageFilter(field), null)

    for (i in filters.size - 1 downTo 0) {
        val filter = filters[i]
        chain = SystemMessageFilterChain(filter, chain)
    }

    return FilterSessionSystemMessage(controller, chain)
}

internal fun createSystemMessage (
    controller: Any,
    filters: List<SystemMessageFilter>,
    method: Method,
): ChatSessionSystemMessage {
    var chain = SystemMessageFilterChain(MethodInvokerSystemMessageFilter(method), null)

    for (i in filters.size - 1 downTo 0) {
        val filter = filters[i]
        chain = SystemMessageFilterChain(filter, chain)
    }

    return FilterSessionSystemMessage(controller, chain)
}

private class FilterSessionSystemMessage (
    private val controller: Any,
    private val chain: SystemMessageFilterChain
) : ChatSessionSystemMessage {
    override fun invoke(chatId: UUID): String? {
        val request = SystemMessageRequest(
            chatId = chatId,
            controller = controller,
        )

        val response = chain.proceed(request)
        return response.message
    }
}

private class MethodInvokerSystemMessageFilter (
    private val method: Method
) : SystemMessageFilter {

    override fun apply(
        request: SystemMessageRequest,
        next: SystemMessageFilterChain
    ): SystemMessageResponse {
        val controller = request.controller

        val message = method.invoke(controller) as String?
        return SystemMessageResponse(message)
    }
}

private class FieldInvokerSystemMessageFilter (
    private val field: Field
): SystemMessageFilter {

    override fun apply(
        request: SystemMessageRequest,
        next: SystemMessageFilterChain
    ): SystemMessageResponse {
        val controller = request.controller

        val message = field.get(controller) as String?
        return SystemMessageResponse(message)
    }
}
