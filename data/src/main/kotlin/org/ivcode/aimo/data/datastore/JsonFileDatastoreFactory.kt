package org.ivcode.aimo.data.datastore

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.ObjectMapper
import org.ivcode.common.data.datastore.Datastore
import org.ivcode.common.data.datastore.DatastoreFactory
import org.ivcode.common.data.datastore.PROPERTY_NAME_DATASTORE_TYPE
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component
import java.io.File

@Component
@ConditionalOnProperty(name = [PROPERTY_NAME_DATASTORE_TYPE], havingValue = "file", matchIfMissing = true)
class JsonFileDatastoreFactory(
    private val root: File = File("./data/datastore"),
    private val objectMapper: ObjectMapper,
): DatastoreFactory {
    override fun <V> createDatastore(name: String, type: Class<V>): Datastore<V> {
        val basePath = File(root, name)
        val javaType: JavaType = objectMapper.typeFactory.constructType(type)

        return JsonFileDatastore(basePath, objectMapper, javaType)
    }
}
