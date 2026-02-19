package org.ivcode.ai.plugin.api.chat.controller

import org.ivcode.ai.plugin.api.chat.session.ChatSessionSystemMessage
import org.ivcode.ai.plugin.api.chat.language.ToolType
import org.ivcode.ai.plugin.api.chat.language.ToolParameter as LanguageToolParameter
import org.ivcode.ai.plugin.api.chat.utils.getAnnotation
import org.ivcode.ai.plugin.api.chat.utils.getFields
import org.ivcode.ai.plugin.api.chat.utils.getMethods
import org.ivcode.ai.plugin.api.chat.utils.isKotlinNullable
import org.jetbrains.annotations.NotNull
import org.ivcode.ai.plugin.api.chat.controller.Tool as ToolAnnotation
import org.ivcode.ai.plugin.api.chat.controller.SystemMessage as SystemMessageAnnotation
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.lang.reflect.Parameter
import kotlin.text.ifEmpty

fun parseChatController(
    controller: Any,
    name: String = controller::class.java.simpleName,
): ChatControllerRegistryEntry {
    // Make sure the @ChatController annotation exists
    controller.getAnnotation(ChatController::class.java) ?: throw IllegalArgumentException("Controller is missing @ChatController annotation")

    val systemMessages = controller.getSystemMessages()
    val methodTools = controller.getTools()

    return ChatControllerRegistryEntry (
        name = name,
        instance = controller,
        systemMessages = systemMessages,
        tools = methodTools
    )
}

private fun Any.getSystemMessages(): ChatControllerSystemMessages {
    val fieldSystemMessages = mutableListOf<Field>()
    val methodSystemMessages = mutableListOf<Method>()

    // Parse fields marked with @SystemMessage
    getFields().forEach { field ->
        field.getAnnotation(SystemMessageAnnotation::class.java) ?: return@forEach

        if (field.type.equals(String::class)) {
            throw IllegalArgumentException("Property ${field.name} marked as @SystemMessage must be of type String")
        }

        field.isAccessible = true
        fieldSystemMessages.add(field)
    }

    // Parse methods with @SystemMessage
    getMethods().forEach { method ->
        method.getAnnotation(SystemMessageAnnotation::class.java) ?: return@forEach

        if(method.parameters.isNotEmpty()) {
            throw IllegalArgumentException("Function ${method.name} marked as @SystemMessage must have a receiver")
        }

        if (method.returnType != String::class.java) {
            throw IllegalArgumentException("Function ${method.name} marked as @SystemMessage must be of type String")
        }

        methodSystemMessages.add(method)
    }

    return ChatControllerSystemMessages (
        fieldMessages = fieldSystemMessages,
        methodMessages = methodSystemMessages
    )
}

private fun Any.getSystemMessage(): List<ChatSessionSystemMessage> {
    val systemMessages = mutableListOf<ChatSessionSystemMessage>()

    // Parse fields marked with @SystemMessage
    getFields().forEach { field ->
        if(field.getAnnotation(SystemMessageAnnotation::class.java) != null) {
            systemMessages.add(field.getSystemMessage(this))
        }
    }

    // Parse methods with @SystemMessage
    getMethods().forEach { method ->
        if(method.getAnnotation(SystemMessageAnnotation::class.java) != null) {
            systemMessages.add(method.getSystemMessage(this))
        }
    }

    return systemMessages
}

private fun Field.getSystemMessage(controller: Any): ChatSessionSystemMessage {
    getAnnotation(SystemMessageAnnotation::class.java) ?: throw IllegalArgumentException("Field ${this.name} missing @SystemMessage annotation")

    if (type != String::class.java) {
        throw IllegalArgumentException("Property $name marked as @SystemMessage must be of type String")
    }

    isAccessible = true

    return SystemMessageGetter(
        receiver = controller,
        getter = FieldGetter(this),
    )
}

private fun Method.getSystemMessage(controller: Any): ChatSessionSystemMessage {
    getAnnotation(SystemMessageAnnotation::class.java) ?: throw IllegalArgumentException("Method ${this.name} missing @SystemMessage annotation")

    if(this.parameters.isNotEmpty()) {
        throw IllegalArgumentException("Function $name marked as @SystemMessage must have a receiver")
    }

    if (returnType != String::class.java) {
        throw IllegalArgumentException("Function $name marked as @SystemMessage must be of type String")
    }

    return SystemMessageGetter(
        receiver = controller,
        getter = MethodGetter(this),
    )
}

private fun Any.getTools(): ChatControllerTools {
    val methodTools = mutableListOf<MethodToolInfo>()

    getMethods().forEach { method ->
        method.getAnnotation(ToolAnnotation::class.java) ?: return@forEach
        methodTools.add(method.createMethodToolInfo())
    }

    return ChatControllerTools (
        methodTools = methodTools
    )
}

private fun Method.createMethodToolInfo(): MethodToolInfo {
    val annotation = getAnnotation(ToolAnnotation::class.java) ?: throw IllegalArgumentException("Method ${this.name} missing @Tool annotation")

    val name = annotation.name.ifEmpty { this.name }
    val description = annotation.description

    val properties = mutableListOf<LanguageToolParameter>()
    this.parameters.forEach { param ->
        properties.add(param.createToolProperty())
    }

    return MethodToolInfo (
        name = name,
        description = description,
        parameters = properties,
        method = this,
    )
}

private fun Parameter.createToolProperty(): LanguageToolParameter {
    val annotation = getAnnotation(ToolParameter::class.java) ?: throw IllegalArgumentException("Property ${this.name} marked as @ToolParameter annotation")

    val nullable = annotation.nullable &&
            this.getAnnotation(NotNull::class.java)!=null ||
            this.isKotlinNullable()

    val isOfArrayType = this.type.isOfArrayType()

    return LanguageToolParameter(
        name = annotation.name.ifEmpty { this.name },
        type = this.type.asToolType(isOfArrayType),
        description = annotation.description.ifEmpty { null },
        isNullable = nullable,
        isArray = isOfArrayType,
        enum = this.type.getEnumValues(),
    )
}

private fun Class<*>.asToolType(isOfArrayType: Boolean): ToolType {
    val type = if(isOfArrayType) {
        getArrayComponentType()
    } else {
        this
    }

    return when(type) {
        Boolean::class.java, java.lang.Boolean::class.java -> ToolType.BOOLEAN
        Int::class.java, Integer::class.java -> ToolType.INTEGER
        Float::class.java, java.lang.Float::class.java -> ToolType.FLOAT
        String::class.java -> ToolType.STRING
        else -> throw IllegalArgumentException("Unsupported tool parameter type: ${this.name}")
    }
}

private fun Class<*>.getEnumValues(): List<String>? {
    if(!this.isEnum) return null
    return this.enumConstants.map { (it as Enum<*>).name }
}

private fun Class<*>.isOfArrayType(): Boolean {
    return this.isArray ||
            (this.isAssignableFrom(List::class.java) && this.typeParameters.size == 1) ||
            (this.isAssignableFrom(Set::class.java) && this.typeParameters.size == 1) ||
            (this.isAssignableFrom(Collection::class.java) && this.typeParameters.size == 1)
}

private fun Class<*>.getArrayComponentType(): Class<*> {
    return when {
        this.isArray -> this.componentType
        this.isAssignableFrom(List::class.java) && this.typeParameters.size == 1 -> this.typeParameters[0].javaClass
        this.isAssignableFrom(Set::class.java) && this.typeParameters.size == 1 -> this.typeParameters[0].javaClass
        this.isAssignableFrom(Collection::class.java) && this.typeParameters.size == 1 -> this.typeParameters[0].javaClass
        else -> throw IllegalArgumentException("Type ${this.name} is not an array or collection type")
    }
}
