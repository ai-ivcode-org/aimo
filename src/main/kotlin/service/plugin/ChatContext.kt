package org.ivcode.beeboop.service.plugin

import org.ivcode.beeboop.plugin.chat.ChatModel
import org.ivcode.beeboop.plugin.tool.SystemMessage
import org.ivcode.beeboop.plugin.tool.Tool

data class ChatContext(
    val model: ChatModel,
    val staticSystemMessages: List<String>,
    val dynamicSystemMessages: List<SystemMessage>,
    val tools: List<Tool>
)