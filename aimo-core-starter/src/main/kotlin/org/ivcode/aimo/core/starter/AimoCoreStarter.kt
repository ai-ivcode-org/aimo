package org.ivcode.aimo.core.starter

import org.ivcode.ai.plugin.api.chat.context.ChatContext
import org.ivcode.ai.plugin.api.chat.controller.ChatController
import org.ivcode.ai.plugin.api.chat.controller.ChatControllerRegistry
import org.ivcode.ai.plugin.api.chat.language.BasicLanguageModelPool
import org.ivcode.ai.plugin.api.chat.language.LanguageModel
import org.ivcode.ai.plugin.api.chat.language.LanguageModelPool
import org.ivcode.ai.plugin.api.chat.dao.SessionDao
import org.ivcode.ai.plugin.api.chat.dao.SessionFactoryDao
import org.springframework.beans.factory.ObjectProvider
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Scope
import org.springframework.context.annotation.ScopedProxyMode

@Configuration
class AimoCoreStarter {

    @Bean("aimo.core.chatControllers")
    fun getChatControllers(ctx: ApplicationContext): Map<String, Any> {
        return ctx.getBeansWithAnnotation(ChatController::class.java)
    }

    @Bean
    fun createChatControllerRegistry (
        @Qualifier("aimo.core.chatControllers") chatControllers: Map<String, Any>
    ): ChatControllerRegistry {
        val registry = ChatControllerRegistry()
        chatControllers.forEach { (key, value) ->
            registry.register(value, key)
        }

        return registry
    }

    @Bean
    @ConditionalOnMissingBean(LanguageModelPool::class)
    fun languageModelPool(languageModelProvider: ObjectProvider<LanguageModel>): LanguageModelPool {
        val languageModel = languageModelProvider.getIfAvailable()
            ?: throw IllegalStateException("No LanguageModel bean available to back the pool")

        return BasicLanguageModelPool(
            languageModel = languageModel,
            maxPoolSize = 1
        )
    }

    @Bean
    @ConditionalOnMissingBean(SessionFactoryDao::class)
    fun chatSessionManager(): SessionFactoryDao {
        return SessionFactoryDao()
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE, proxyMode = ScopedProxyMode.TARGET_CLASS)
    fun chatContext(): ChatContext {
        return ChatContext.getContext()
    }

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE, proxyMode = ScopedProxyMode.TARGET_CLASS)
    fun chatSession(): SessionDao {
        return ChatContext.getContext().session
    }
}
