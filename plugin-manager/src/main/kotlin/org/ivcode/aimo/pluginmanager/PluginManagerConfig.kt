package org.ivcode.aimo.pluginmanager

import org.ivcode.common.data.storage.Storage
import org.ivcode.common.data.storage.StorageFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.nio.file.Path

private const val MODULE = "plugin-manager"

internal const val NAME_PLUGIN_PATH = "$MODULE.loader.path"
internal const val NAME_PLUGIN_STORAGE = "$MODULE.loader.storage"

internal const val STORAGE_BASE_PATH = "plugins"

@Configuration
class PluginManagerConfig {
    @Bean (NAME_PLUGIN_PATH)
    fun createPluginPath(): Path {
        return Path.of("./data/plugins")
    }

    @Bean(NAME_PLUGIN_STORAGE)
    fun createPluginStorage(
        @Suppress("SpringJavaInjectionPointsAutowiringInspection") factory: StorageFactory
    ): Storage {
        return factory.createStorage(STORAGE_BASE_PATH)
    }
}