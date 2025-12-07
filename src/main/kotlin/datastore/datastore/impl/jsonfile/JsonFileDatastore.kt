package org.ivcode.beeboop.datastore.datastore.impl.jsonfile

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.ivcode.beeboop.datastore.datastore.Datastore
import java.io.File

class JsonFileDatastore <V>(
    private val basePath: File,
    private val objectMapper: ObjectMapper,
    private val typeReference: TypeReference<V>,
): Datastore<V> {

    override fun put(key: String, value: V) {
        val file = File(basePath, "$key.json")
        objectMapper.writeValue(file, value)
    }

    override fun get(key: String): V? {
        val file = File(basePath, "$key.json")
        if (!file.exists()) {
            return null
        }

        return objectMapper.readValue(file, typeReference)
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
            val value = objectMapper.readValue(file, typeReference)
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