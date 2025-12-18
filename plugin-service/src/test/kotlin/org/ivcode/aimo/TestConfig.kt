package org.ivcode.aimo

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.ivcode.aimo.data.datastore.JsonFileDatastoreFactory
import org.ivcode.aimo.data.session.EhcacheSessionFactory
import org.ivcode.aimo.data.storage.FileStorageFactory
import org.ivcode.common.data.datastore.DatastoreFactory
import org.ivcode.common.data.session.SessionFactory
import org.ivcode.common.data.storage.StorageFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.File
import java.nio.file.Path

internal const val BASE_PATH = "./test-data"

internal const val DATASTORE_ROOT_PATH = "$BASE_PATH/datastore"
internal const val STORAGE_ROOT_PATH = "$BASE_PATH/plugin-store"
internal const val PLUGIN_LOCAL = "$BASE_PATH/plugin-local"

@Configuration
class TestConfig {

    @Bean
    fun createObjectMapper(): ObjectMapper = jacksonObjectMapper()

    @Bean
    fun createDatastoreFactory(
        objectMapper: ObjectMapper,
    ): DatastoreFactory = JsonFileDatastoreFactory (
        root = File(DATASTORE_ROOT_PATH),
        objectMapper = objectMapper,
    )

    @Bean
    fun createSessionFactory(): SessionFactory = EhcacheSessionFactory()

    @Bean
    fun createStorageFactory(): StorageFactory = FileStorageFactory(
        Path.of(STORAGE_ROOT_PATH),
    )
}