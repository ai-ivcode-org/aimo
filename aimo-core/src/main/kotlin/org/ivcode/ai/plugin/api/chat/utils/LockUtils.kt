package org.ivcode.ai.plugin.api.chat.utils

import java.util.concurrent.locks.Lock

internal fun <T> Lock.use(block: () -> T): T {
    this.lock()
    try {
        return block()
    } finally {
        this.unlock()
    }
}