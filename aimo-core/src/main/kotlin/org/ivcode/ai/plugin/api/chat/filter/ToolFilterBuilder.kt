package org.ivcode.ai.plugin.api.chat.filter

import org.ivcode.ai.plugin.api.chat.language.ToolInfo
import org.ivcode.ai.plugin.api.chat.session.ChatSessionToolExecutor
import java.lang.reflect.Method
import java.util.UUID


internal fun createToolExecutor (
    controller: Any,
    filters: List<ToolFilter>,
    method: Method,
    propertyNames: List<String>
): ChatSessionToolExecutor {
    val toolFilterChain = createToolFilterChain(filters, method, propertyNames)
    return FilterSessionToolExecutor(controller, toolFilterChain)
}

private fun createToolFilterChain (filters: List<ToolFilter>, method: Method, propertyNames: List<String>): ToolFilterChain {
    var chain = ToolFilterChain(MethodInvokerToolFilter(method, propertyNames), null)

    for (i in filters.size - 1 downTo 0) {
        val filter = filters[i]
        chain = ToolFilterChain(filter, chain)
    }

    return chain
}

private class MethodInvokerToolFilter (
    val method: Method,
    val propertyNames: List<String>
): ToolFilter {

    override fun apply(request: ToolRequest, next: ToolFilterChain): ToolResponse {
        val controller = request.controller
        val parameters = request.params

        val args = createArgumentArray(propertyNames, parameters)

        val message = method.invoke(controller, *args) as String
        return ToolResponse(message)
    }
}

private class FilterSessionToolExecutor (
    private val controller: Any,
    private val toolFilterChain: ToolFilterChain
): ChatSessionToolExecutor {
    override fun invoke(
        chatId: UUID,
        toolInfo: ToolInfo,
        parameters: Map<String, Any?>
    ): String {
        val request = ToolRequest (
            chatId = chatId,
            toolInfo = toolInfo,
            params = parameters,
            controller = controller
        )

        val response = toolFilterChain.proceed(request)
        return response.message
    }
}

private fun createArgumentArray (
    propertyNames: List<String>,
    parameters: Map<String, Any?>
): Array<Any?> {
    val args = arrayOfNulls<Any?>(propertyNames.size)
    propertyNames.forEachIndexed { index, property ->
        args[index] = parameters[property]
    }

    return args
}
