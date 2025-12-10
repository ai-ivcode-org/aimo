package org.ivcode.aimo.plugin.tool

data class ToolSchema (
    val name: String,
    val description: String?,
    val properties: Map<String, ToolSchemaProperty>,
)

interface ToolSchemaProperty {
    val type: ToolSchemaType
    val description: String?
    val required: Boolean
}

data class BasicToolSchemaProperty (
    override val type: ToolSchemaType,
    override val description: String?,
    override val required: Boolean
) : ToolSchemaProperty {
    init {
        require(type != ToolSchemaType.OBJECT && type != ToolSchemaType.ARRAY)
    }
}

data class ObjectToolSchemaProperty (
    override val description: String?,
    override val required: Boolean,
    val properties: Map<String, ToolSchemaProperty>
) : ToolSchemaProperty {
    override val type: ToolSchemaType = ToolSchemaType.OBJECT
}

data class ArrayToolSchemaProperty (
    override val description: String?,
    override val required: Boolean,
    val items: ToolSchemaProperty
) : ToolSchemaProperty {
    override val type: ToolSchemaType = ToolSchemaType.ARRAY
}

enum class ToolSchemaType {
    STRING,
    NUMBER,
    INTEGER,
    BOOLEAN,
    OBJECT,
    ARRAY,
}
