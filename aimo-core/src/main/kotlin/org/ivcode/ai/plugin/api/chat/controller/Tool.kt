package org.ivcode.ai.plugin.api.chat.controller

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class Tool (
    val name: String = "",
    val description: String
)

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class ToolParameter (
    val name: String = "",
    val description: String = "",
    val nullable: Boolean = true,
    val enum: Array<String> = []
)