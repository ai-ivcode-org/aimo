package org.ivcode.beeboop.service.plugin.config

import org.ivcode.beeboop.datastore.storage.Storage
import org.ivcode.beeboop.datastore.storage.StorageFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

const val BEAN_PREFIX_PLUGIN_SERVICE = "pluginService"
const val BEAN_PLUGIN_PACKAGE_STORE = "$BEAN_PREFIX_PLUGIN_SERVICE.pluginPackageStore"

@Configuration
class PluginServiceConfig {

    @Bean (BEAN_PLUGIN_PACKAGE_STORE)
    fun pluginPackageStore(
        storageFactory: StorageFactory
    ): Storage {
        return storageFactory.createStorage("plugin-packages")
    }
}