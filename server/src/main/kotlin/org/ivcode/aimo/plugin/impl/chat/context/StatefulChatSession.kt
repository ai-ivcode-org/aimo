package org.ivcode.aimo.plugin.impl.chat.context

import org.ivcode.ai.plugin.api.chat.dao.SessionDao
import org.ivcode.ai.plugin.api.chat.language.ChatCallback
import org.ivcode.ai.plugin.api.chat.language.ChatRequest
import org.ivcode.ai.plugin.api.chat.language.ChatResult

interface StatefulChatSession : SessionDao {
    fun chat (request: ChatRequest, callback: ChatCallback?): ChatResult
}