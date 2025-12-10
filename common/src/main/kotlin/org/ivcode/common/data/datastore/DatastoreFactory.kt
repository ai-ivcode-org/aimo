package org.ivcode.common.data.datastore

interface DatastoreFactory {
    fun <V> createDatastore(name: String, type: Class<V>): Datastore<V>
}