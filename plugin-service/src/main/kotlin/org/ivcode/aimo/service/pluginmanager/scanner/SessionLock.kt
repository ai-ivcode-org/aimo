package org.ivcode.aimo.service.pluginmanager.scanner

import org.ivcode.common.data.session.Session


internal fun <R> Session<String>.tryLock(key: String, acquired: ()-> R): R = tryLock(key, acquired) {
    throw IllegalStateException("Could not acquire lock for key: $key")
}

internal fun <R> Session<String>.tryLock(path: String, acquired: () -> R, unavailable: () -> R): R {
    try {
        // if successful, lock acquired.
        // If something happens and an unmanaged lock exists, it will expire after the session TTL
        putIfAbsent(path, path)
    } catch (_: Exception) {
        // already locked by another process
        return unavailable()
    }

    try {
        return acquired()
    } finally {
        remove(path)
    }
}