package org.ivcode.beeboop.datastore.session

/**
 * A session is an external cache used to hold session-scoped information.
 *
 * Implementations back this interface with some external store (in-memory,
 * distributed cache, persistent store, etc.). The interface is intentionally
 * minimal — callers get/put/remove values by string key. Implementations may
 * impose additional semantics (expiration, eviction, serialization) which are
 * documented on the concrete implementation.
 *
 * Type parameter T represents the stored value type. The `get` method may
 * return null when no value is associated with the given key.
 */
interface Session<T> {

    /**
     * Store the provided [value] under the specified [key]. If a value already
     * exists for the key it should be replaced.
     *
     * Implementations may throw runtime exceptions when the underlying store
     * fails (I/O, serialization, network errors, etc.). Callers should
     * consider handling such exceptions as appropriate for their context.
     *
     * @param key non-null string key identifying the session entry
     * @param value non-null value to store; implementations may serialize or
     * transform the value depending on the backing store
     */
    fun put(key: String, value: T)

    /**
     * Retrieve the value associated with [key]. Returns null if no value is
     * present for the key.
     *
     * Implementations may return a cached or deserialized instance. There is
     * no guaranteed reference or immutability semantics — callers should treat
     * the returned value according to the implementation's contract.
     *
     * @param key non-null string key
     * @return the stored value or null when absent
     */
    fun get(key: String): T?

    /**
     * Remove any value associated with [key]. If no value exists this method
     * should be a no-op.
     *
     * Implementations may throw runtime exceptions when the underlying store
     * fails; otherwise the operation should succeed silently if the key is
     * missing.
     *
     * @param key non-null string key to remove
     */
    fun remove(key: String)
}