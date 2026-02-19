package org.ivcode.ai.ollama.language

import org.ivcode.ai.plugin.api.chat.language.ToolInfo as AimoToolInfo
import org.ivcode.ai.ollama.client.Tool as OllamaTool

internal data class OllamaAimoTool (
    val ollamaTool: OllamaTool,
    val aimoToolInfo: AimoToolInfo,
)