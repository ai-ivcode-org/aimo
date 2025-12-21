package org.ivcode.ai.ollama.system

class BasicSystemMessageFactory(
    private val systemMessages: List<OllamaSystemMessage>
): OllamaSystemMessageFactory {
    override fun createSystemMessages(): List<OllamaSystemMessage> = systemMessages
}