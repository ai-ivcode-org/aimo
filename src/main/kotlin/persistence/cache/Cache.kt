package org.ivcode.beeboop.persistence.cache

/**
 * A simple cache interface for storing and retrieving key-value pairs. This is temporary storage and cannot be relied
 * upon for long term persistence.
 *
 * This is an abstraction layer so that the implementation may scale from local file system to distributed file systems
 * as needed.
 */
interface Cache {
    fun put(key: String, value: String)
    fun get(key: String): String?
    fun remove(key: String)
}