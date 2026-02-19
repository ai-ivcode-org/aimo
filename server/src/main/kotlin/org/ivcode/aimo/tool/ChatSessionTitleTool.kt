package org.ivcode.aimo.tool

import io.github.ollama4j.tools.annotations.ToolProperty
import io.github.ollama4j.tools.annotations.ToolSpec
import org.ivcode.ai.ollama.annotations.OllamaController
import org.ivcode.ai.ollama.annotations.SystemMessage

const val TOOL_NAME = "set_chat_title"

@OllamaController
class ChatSessionTitleTool {

    @SystemMessage
    val systemMessage = """
        You have access to a tool that allows you to set the title of the current chat session. The name of the tool is "$TOOL_NAME".
        Use this tool to update the chat title as needed. If the current title is not appropriate for the conversation, set a new one that better reflects the content of the chat.
        Only use this tool when you need to change the chat session title.
    """.trimIndent()

    @ToolSpec(
        name = TOOL_NAME,
        desc = "Sets the title of the current chat session."
    )
    fun setTitle(
        @ToolProperty(name = "title", desc = "the new title for the current chat session", required = true) title: String
    ): String {
        // Logic to set the chat session title
        return "Title set to: $title"
    }
}