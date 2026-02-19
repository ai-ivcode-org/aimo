package org.ivcode.ai.ollama.language

import org.ivcode.ai.ollama.client.Items
import org.ivcode.ai.ollama.client.Type
import org.ivcode.ai.ollama.client.Function as OllamaFunction
import org.ivcode.ai.ollama.client.Parameters as OllamaParameters
import org.ivcode.ai.ollama.client.Property as OllamaProperty
import org.ivcode.ai.ollama.client.Tool as OllamaTool
import org.ivcode.ai.plugin.api.chat.language.ToolInfo as AimoToolInfo
import org.ivcode.ai.plugin.api.chat.language.ToolParameter as AimoToolToolParameter
import org.ivcode.ai.plugin.api.chat.language.ToolType as AimoToolType

internal fun AimoToolInfo.toOllamaAimoTool() = OllamaAimoTool (
    ollamaTool = toOllamaTool(),
    aimoToolInfo = this
)

private fun AimoToolInfo.toOllamaTool() = OllamaTool (
    function = toOllamaFunction()
)

private fun AimoToolInfo.toOllamaFunction() = OllamaFunction(
    name = this.name,
    description = this.description,
    parameters = this.parameters.toOllamaParameters(),
)

private fun List<AimoToolToolParameter>.toOllamaParameters() = OllamaParameters (
    required = this.filter { !it.isNullable }.map { it.name },
    properties = this.associate { it.name to it.toOllamaProperty() },
)

private fun AimoToolToolParameter.toOllamaProperty(): OllamaProperty {
    val type: Type
    val items: Items?

    if (this.isArray) {
        type = Type.ARRAY

        val arrayType = this.type.toOllamaType()
        items = Items(
            type = arrayType,
            enum = if (arrayType == Type.STRING) this.enum else null
        )
    } else {
        type = this.type.toOllamaType()
        items = null
    }

    return OllamaProperty(
        type = type,
        items = items,
        description = this.description,
        enum = if (type == Type.STRING) this.enum else null
    )
}

private fun AimoToolType.toOllamaType(): Type = when (this) {
    AimoToolType.BOOLEAN -> Type.BOOLEAN
    AimoToolType.INTEGER -> Type.INTEGER
    AimoToolType.FLOAT -> Type.NUMBER
    AimoToolType.STRING -> Type.STRING
}
