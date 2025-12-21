package org.ivcode.ai.ollama.system

interface OllamaSystemMessageFactory {
    fun createSystemMessages(): List<OllamaSystemMessage>
}