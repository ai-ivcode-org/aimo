package org.ivcode.ai.ollama.system

/**
 * A function that provides information (dynamic or static) to the LLM as a system message.
 *
 * Use Cases Include:
 *  - guardrails
 *  - system status
 *  - user information
 *
 * These function should be lightweight and not require significant computation or I/O.
 * For more complex data retrieval, use a tool call instead.
 */
fun interface OllamaSystemMessage {
    operator fun invoke(): String?
}
