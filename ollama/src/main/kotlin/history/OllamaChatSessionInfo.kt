package org.ivcode.ai.ollama.history

import java.util.UUID

data class OllamaChatSessionInfo (
    val id: UUID,
    var title: String? = null
)