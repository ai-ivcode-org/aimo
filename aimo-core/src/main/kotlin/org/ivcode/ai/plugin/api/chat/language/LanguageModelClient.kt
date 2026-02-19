package org.ivcode.ai.plugin.api.chat.language

/**
 * Client interface for interacting with a language model to perform chat-style requests.
 *
 * This interface is not thread-safe: each instance represents a single connection and
 * may retain connection-specific resources or state. Callers must not share a single
 * instance concurrently across threads; create separate instances per connection or
 * synchronize access externally.
 *
 * Implementations of this interface are responsible for sending the provided [ChatRequest]
 * together with the supplied [ChatRequestState] (system messages, conversation history and available tools)
 * to a language model backend and returning a [ChatResult]. If streaming output is supported,
 * the optional [ChatCallback] may be used to receive intermediate tokens/updates.
 */
interface LanguageModelClient {

    /**
     * Run a chat request against the language model.
     *
     * Note: implementations may rely on per-instance connection state. Do not call this
     * method concurrently on the same instance from multiple threads unless external
     * synchronization is provided.
     *
     * @param request the chat request containing the user prompt and streaming preference.
     * @param state the current chat state including system messages, prior history and tools.
     * @param callback optional callback to receive streaming updates (may be null if not used).
     * @return a [ChatResponse] representing the model's response (or aggregated response when streaming).
     */
    fun chat (
        request: ChatRequest,
        callback: ChatCallback?
    ): ChatResponse
}
