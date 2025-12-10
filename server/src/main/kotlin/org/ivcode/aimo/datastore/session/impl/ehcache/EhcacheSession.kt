package org.ivcode.aimo.datastore.session.impl.ehcache

import org.ehcache.Cache
import org.ivcode.common.data.session.Session

/**
 * Ehcache-backed implementation of the plugin persistence Cache.
 *
 * The Ehcache `Cache<String, T>` instance is provided by the caller. This class
 * stores values directly in the cache (no JSON serialization/deserialization).
 *
 * Responsibilities / contract:
 * - Inputs: keys are Strings, values are of type T.
 * - Outputs: returns stored T instances or null when missing.
 * - Error modes: this implementation does not catch runtime exceptions from the
 *   underlying Ehcache; callers can handle or configure the cache appropriately.
 *
 * Notes and caveats:
 * - Because values are stored as objects directly, if the cache is configured
 *   to use off-heap/disk persistence the caller must ensure appropriate
 *   serializers are configured for type T.
 * - This class intentionally does not attempt any serialization. If you need
 *   JSON or cross-JVM portability, wrap the cache or provide a different
 *   Cache implementation that performs serialization.
 */
class EhcacheSession<T>(
    private val cache: Cache<String, T>
) : Session<T> {

    override fun put(key: String, value: T) {
        cache.put(key, value)
    }

    override fun get(key: String): T? {
        return cache.get(key)
    }

    override fun remove(key: String) {
        cache.remove(key)
    }
}