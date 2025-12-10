package org.ivcode.aimo.plugin.tool

interface Tool {
    val name: String
    val description: String?
    val schema: ToolSchema
    fun apply(params: Map<String, Any>): String
}