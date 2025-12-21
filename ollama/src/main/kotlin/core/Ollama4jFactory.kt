package org.ivcode.ai.ollama.core

import com.fasterxml.jackson.databind.DeserializationFeature
import io.github.ollama4j.Ollama
import io.github.ollama4j.tools.Tools
import io.github.ollama4j.utils.Utils

class Ollama4jFactory (
    private val url: String,
    private val model: String?,
    private val requestTimeoutSeconds: Long?,
    private val toolServices:  Map<String, Any>?,
    private val tools: List<Tools.Tool>?
): OllamaFactory {
    override fun createOllama() = Ollama(url).apply {
        // Workaround
        Utils.getObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)

        // If defined, set the request timeout
        if (requestTimeoutSeconds != null) {
            setRequestTimeoutSeconds(requestTimeoutSeconds)
        }

        // pull the model to ensure it's available
        if(model != null) {
            pullModel(model)
        }

        // register tools
        toolServices?.entries?.forEach {
            this.registerAnnotatedTools(it.value)
        }
        tools?.forEach { tool ->
            this.registerTool(tool)
        }
    }
}