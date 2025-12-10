package org.ivcode.aimo.plugin.tool

data class ToolPlugin (
    val id: String,
    val name: String,
    val description: String?,
    val systemMessages: List<SystemMessage>,
    val tools: List<Tool>,
) {
    class Builder (
        val id: String,
        val name: String,
        val description: String?
    ) {
        private val systemMessages = mutableListOf<SystemMessage>()
        private val tools = mutableListOf<Tool>()

        fun withSystemMessage(message: String): Builder {
            systemMessages.add { message }
            return this
        }

        fun withSystemMessage(systemMessage: SystemMessage): Builder {
            systemMessages.add(systemMessage)
            return this
        }

        fun withTool(tool: Tool): Builder {
            tools.add(tool)
            return this
        }

        fun build(): ToolPlugin {
            return ToolPlugin(
                id = id,
                name = name,
                description = description,
                systemMessages = systemMessages,
                tools = tools
            )
        }
    }
}