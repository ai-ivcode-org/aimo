package org.ivcode.common.data.datastore

import org.ivcode.common.PROPERTY_PREFIX_APP_NAME

const val PROPERTY_NAME_DATASTORE_TYPE = "$PROPERTY_PREFIX_APP_NAME.datastore.type"

/**
 * Generic interface for a persistent key/value datastore
 */
interface Datastore<V> {
    fun put(key: String, value: V)
    fun get(key: String): V?
    fun listKeys(): List<String>
    fun list(): Map<String, V>
    fun delete(key: String)
}