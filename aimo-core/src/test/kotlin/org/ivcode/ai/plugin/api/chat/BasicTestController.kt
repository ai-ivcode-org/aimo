package org.ivcode.ai.plugin.api.chat

import org.ivcode.ai.plugin.api.chat.controller.ChatController
import org.ivcode.ai.plugin.api.chat.controller.SystemMessage
import org.ivcode.ai.plugin.api.chat.controller.Tool
import org.ivcode.ai.plugin.api.chat.controller.ToolParameter

@ChatController
class BasicTestController {

    @SystemMessage
    val systemMessage = "System message from BasicTestController"

    @SystemMessage
    fun systemMessageFunction(): String {
        return "System message from function in BasicTestController"
    }

    @Tool (description = "Echoes the input string back to the caller")
    fun echoTool(
        @ToolParameter (description = "the value to be echoed") input: String
    ): String {
        return input
    }
}