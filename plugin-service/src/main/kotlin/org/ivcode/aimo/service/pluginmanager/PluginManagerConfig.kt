package org.ivcode.aimo.service.pluginmanager

import org.ivcode.aimo.service.pluginmanager.info.PluginInfoService
import org.ivcode.aimo.service.pluginmanager.info.PluginInfoServiceDatastore
import org.ivcode.aimo.service.pluginmanager.model.PluginInfo
import org.ivcode.common.data.datastore.Datastore
import org.ivcode.common.data.datastore.DatastoreFactory
import org.ivcode.common.data.session.Session
import org.ivcode.common.data.session.SessionFactory
import org.ivcode.common.data.storage.Storage
import org.ivcode.common.data.storage.StorageFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import java.nio.file.Path

private const val MODULE = "plugin-manager"

internal const val PLUGIN_CACHE_PATH = "$MODULE.plugin-cache.path"
internal const val PLUGIN_STORAGE = "$MODULE.loader.storage"

internal const val STORAGE_BASE_PATH = "plugins"

internal const val INFO_DATASTORE = "$MODULE.info.datastore"

internal const val LOCK_SESSION = "$MODULE.lock"

@Configuration
@ComponentScan
internal class PluginManagerConfig {

    @Bean
    fun createInfoService(
        @Qualifier(INFO_DATASTORE) datastore: Datastore<PluginInfo>
    ): PluginInfoService {
        return PluginInfoServiceDatastore(
            datastore = datastore,
        )
    }

    @Bean (INFO_DATASTORE)
    fun createInfoDatastore(
        @Suppress("SpringJavaInjectionPointsAutowiringInspection") factory: DatastoreFactory
    ): Datastore<PluginInfo> {
        return factory.createDatastore(
            name = "$MODULE.plugin-info",
            type = PluginInfo::class.java,
        )
    }

    @Bean (LOCK_SESSION)
    fun createLockSession(
        @Suppress("SpringJavaInjectionPointsAutowiringInspection") factory: SessionFactory
    ): Session<String> = factory.createCache (
        name = "$MODULE.lock-session",
        type = String::class.java,
    )

    @Bean (PLUGIN_CACHE_PATH)
    fun createPluginPath(): Path {
        return Path.of("./data/plugin-cache")
    }

    @Bean(PLUGIN_STORAGE)
    fun createPluginStorage(
        @Suppress("SpringJavaInjectionPointsAutowiringInspection") factory: StorageFactory
    ): Storage {
        return factory.createStorage()
    }
}