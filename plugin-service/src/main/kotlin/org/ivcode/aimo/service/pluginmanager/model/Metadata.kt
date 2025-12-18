package org.ivcode.aimo.service.pluginmanager.model

data class Metadata (
    val id: String,
    val name: String,
    val description: String?,
    val version: String?,
    val entryPoint: String,
)