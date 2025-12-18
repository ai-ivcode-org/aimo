package org.ivcode.aimo.data.datastore

import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.ObjectMapper
import org.ivcode.common.data.datastore.Datastore
import java.io.File

class JsonFileDatastore <V>(
    private val basePath: File,
    private val objectMapper: ObjectMapper,
    private val javaType: JavaType,
): Datastore<V> {

    override fun put(key: String, value: V) {
        // TODO make sure the key is not trying to escape the base path
        val file = File(basePath, "$key.json")
        file.parentFile.mkdirs()

        objectMapper.writeValue(file, value)
    }

    override fun get(key: String): V? {
        val file = File(basePath, "$key.json")
        if (!file.exists()) {
            return null
        }

        val value = objectMapper.readValue<V>(file, javaType)
        return value
    }

    override fun listKeys(): List<String> {
        return basePath.listFiles()
            ?.filter { it.isFile && it.extension == "json" }
            ?.map { it.nameWithoutExtension }
            ?: emptyList()
    }

    override fun list(): Map<String, V> {
        val result = mutableMapOf<String, V>()
        val files = basePath.listFiles()
            ?.filter { it.isFile && it.extension == "json" }
            ?: emptyList()

        for (file in files) {
            val key = file.nameWithoutExtension
            val value = objectMapper.readValue<V>(file, javaType)
            result[key] = value
        }

        return result
    }

    override fun delete(key: String) {
        val file = File(basePath, "$key.json")
        if (file.exists()) {
            file.delete()
        }
    }
}