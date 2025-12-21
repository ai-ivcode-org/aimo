package org.ivcode.ai.ollama.system

import kotlin.reflect.KFunction

class KFunctionSystemMessage(
    private val instance: Any,
    private val function: KFunction<Any?>
): OllamaSystemMessage {
    override fun invoke(): String? {
        return function.call(instance)?.toString()
    }
}