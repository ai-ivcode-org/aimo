package org.ivcode.ai.ollama.system

import kotlin.reflect.KProperty

class KPropertySystemMessage(
    private val instance: Any,
    private val property: KProperty<*>,
): OllamaSystemMessage {
    override fun invoke(): String? = property.call(instance)?.toString()
}