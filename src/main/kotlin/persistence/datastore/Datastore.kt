package org.ivcode.beeboop.persistence.datastore


/**
 * A datastore for persisting and retrieving data. This is a more permanent storage solution compared to a cache, but
 * also slower.
 *
 * This is an abstraction layer so that the implementation may scale from local file system to distributed file systems
 * as needed.
 */
interface Datastore {
    fun put(collection: String, key: String, value: String)
    fun get(collection: String, key: String): String?
    fun remove(collection: String, key: String)
}