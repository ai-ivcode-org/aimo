package org.ivcode.ai.plugin.api.chat.controller

import org.ivcode.ai.plugin.api.chat.filter.SystemMessageFilter
import org.ivcode.ai.plugin.api.chat.filter.ToolFilter
import org.ivcode.ai.plugin.api.chat.filter.createToolExecutor
import org.ivcode.ai.plugin.api.chat.filter.createSystemMessage
import org.ivcode.ai.plugin.api.chat.language.ToolInfo
import org.ivcode.ai.plugin.api.chat.language.ToolParameter
import org.ivcode.ai.plugin.api.chat.session.ChatSessionSystemMessage
import org.ivcode.ai.plugin.api.chat.session.ChatSessionTool
import java.lang.reflect.Field
import java.lang.reflect.Method

class ChatControllerRegistry {
    private val entries = mutableMapOf<String, ChatControllerRegistryEntry>()
    private val toolFilters = mutableListOf<ToolFilter>()
    private val systemMessageFilters = mutableListOf<SystemMessageFilter>()

    fun register (controller: Any, name: String = controller::class.java.simpleName) {
        val entry = parseChatController(
            controller = controller,
            name = name
        )

        val old = entries.put(name, entry)
        if (old != null) {
            throw IllegalStateException("Chat controller of type $name is already registered")
        }
    }

    fun registerToolFilter (filter: ToolFilter) {
        toolFilters.add(filter)
    }

    fun registerSystemMessageFilter (filter: SystemMessageFilter) {
        systemMessageFilters.add(filter)
    }

    fun createSessionTools() : List<ChatSessionTool> {
        val sessionTools = mutableListOf<ChatSessionTool>()

        entries.values.forEach { entry ->
            entry.tools.methodTools.forEach { methodToolInfo ->
                createToolExecutor (
                    controller = entry.instance,
                    filters = toolFilters,
                    method = methodToolInfo.method,
                    propertyNames = methodToolInfo.parameters.map { it.name }
                ).let { executor ->
                    sessionTools.add(
                        ChatSessionTool(
                            info = ToolInfo(
                                name = methodToolInfo.name,
                                description = methodToolInfo.description,
                                parameters = methodToolInfo.parameters,
                            ),
                            executor = executor
                        )
                    )
                }
            }
        }

        return sessionTools
    }

    fun createSessionSystemMessages(): List<ChatSessionSystemMessage> {
        val sessionSystemMessages = mutableListOf<ChatSessionSystemMessage>()

        entries.values.forEach { entry ->
            entry.systemMessages.fieldMessages.forEach { field ->
                createSystemMessage(
                    controller = entry.instance,
                    filters = systemMessageFilters,
                    field = field,
                ).let { systemMessage ->
                    sessionSystemMessages.add(systemMessage)
                }
            }

            entry.systemMessages.methodMessages.forEach { method ->
                createSystemMessage(
                    controller = entry.instance,
                    filters = systemMessageFilters,
                    method = method,
                ).let { systemMessage ->
                    sessionSystemMessages.add(systemMessage)
                }
            }
        }

        return sessionSystemMessages
    }
}



data class ChatControllerRegistryEntry (
    val name: String,
    val instance: Any,
    val systemMessages: ChatControllerSystemMessages,
    val tools: ChatControllerTools
)

data class ChatControllerSystemMessages (
    val fieldMessages: List<Field>,
    val methodMessages: List<Method>,
)

data class ChatControllerTools (
    val methodTools: List<MethodToolInfo>,
)

data class MethodToolInfo (
    val name: String,
    val description: String,
    val parameters: List<ToolParameter>,
    val method: Method,
)