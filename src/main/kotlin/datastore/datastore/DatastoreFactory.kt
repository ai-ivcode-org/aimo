package org.ivcode.beeboop.datastore.datastore

interface DatastoreFactory {
    fun <V> createDatastore(name: String, type: Class<V>): Datastore<V>
}