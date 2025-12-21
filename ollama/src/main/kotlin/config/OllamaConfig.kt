package org.ivcode.ai.ollama.config

import io.github.ollama4j.tools.Tools
import org.ivcode.ai.ollama.agent.Ollama4jChatAgentFactory
import org.ivcode.ai.ollama.agent.OllamaChatAgentFactory
import org.ivcode.ai.ollama.annotations.OllamaController
import org.ivcode.ai.ollama.controller.ControllerParser
import org.ivcode.ai.ollama.core.Ollama4jFactory
import org.ivcode.ai.ollama.core.OllamaFactory
import org.ivcode.ai.ollama.history.InMemoryHistoryManagerFactory
import org.ivcode.ai.ollama.history.OllamaHistoryManagerFactory
import org.ivcode.ai.ollama.system.BasicSystemMessageFactory
import org.ivcode.ai.ollama.system.OllamaSystemMessage
import org.ivcode.ai.ollama.system.OllamaSystemMessageFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean


const val PROPERTY_URL = "ollama.url"
const val PROPERTY_MODEL_NAME = "ollama.model"
const val PROPERTY_REQUEST_TIMEOUT = "ollama.requestTimeoutSeconds"

@AutoConfiguration
class OllamaConfig(
    @param:Value("\${$PROPERTY_URL:http://localhost:11434}") private val url: String,
    @param:Value("\${$PROPERTY_MODEL_NAME:gpt-oss:20b}") private val model: String,
    @param:Value("\${$PROPERTY_REQUEST_TIMEOUT:#{null}}") private val requestTimeoutSeconds: Long?,
) {

    @Bean("tool_services.system_messages")
    fun createOllamaToolServicesSystemMessages (
        @Qualifier("tool_services") toolControllers: Map<String, Any>,
    ): List<OllamaSystemMessage> {
        val systemMessages = mutableListOf<OllamaSystemMessage>()
        val parser = ControllerParser()

        toolControllers.values.forEach { v ->
            systemMessages.addAll(parser.parseController(v))
        }

        return systemMessages
    }

    @Bean
    @ConditionalOnMissingBean
    fun createSystemMessageFactory(
        @Qualifier("tool_services.system_messages") toolServiceSystemMessages: List<OllamaSystemMessage>,
        systemMessages: List<OllamaSystemMessage>?,
    ): OllamaSystemMessageFactory {
        return BasicSystemMessageFactory (
            systemMessages = (toolServiceSystemMessages + (systemMessages ?: emptyList())).distinct()
        )
    }


    @Bean("tool_services")
    fun ollamaToolServices (ctx: ApplicationContext): Map<String,Any> {
        return ctx.getBeansWithAnnotation(OllamaController::class.java)
    }

    @Bean
    @ConditionalOnMissingBean
    fun createOllamaFactory(
        @Qualifier("tool_services") toolServices: Map<String, Any>,
        tools: List<Tools.Tool>?
    ): OllamaFactory = Ollama4jFactory (
        url = url,
        model = model,
        requestTimeoutSeconds = requestTimeoutSeconds,
        toolServices = toolServices,
        tools = tools
    )

    @Bean
    @ConditionalOnMissingBean
    fun createHistoryManagerFactory(): OllamaHistoryManagerFactory = InMemoryHistoryManagerFactory()

    @Bean
    @ConditionalOnMissingBean
    fun createChatAgentFactory(
        ollamaFactory: OllamaFactory,
        historyManagerFactory: OllamaHistoryManagerFactory,
        systemMessageFactory: OllamaSystemMessageFactory
    ): OllamaChatAgentFactory = Ollama4jChatAgentFactory (
        model = model,
        ollamaFactory = ollamaFactory,
        historyManagerFactory = historyManagerFactory,
        systemMessageFactory = systemMessageFactory
    )
}
