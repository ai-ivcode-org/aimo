package org.ivcode.ai.ollama.core

import io.github.ollama4j.Ollama

interface OllamaFactory {
    fun createOllama(): Ollama
}