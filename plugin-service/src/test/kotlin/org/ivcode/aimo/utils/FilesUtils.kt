package org.ivcode.aimo.utils

import java.nio.file.Files
import java.nio.file.Path
import java.util.Comparator

fun Path.deleteRecursivelyWithRetry(attempts: Int = 5, delayMs: Long = 200): Boolean {
    if (!Files.exists(this)) return true

    repeat(attempts) { attempt ->
        try {
            // Walk and delete files/dirs in reverse order so files are deleted before their parents
            Files.walk(this)
                .sorted(Comparator.reverseOrder())
                .forEach { p ->
                    try {
                        Files.deleteIfExists(p)
                    } catch (ex: Exception) {
                        // Make writable and try once more
                        try { p.toFile().setWritable(true) } catch (_: Throwable) {}
                        Files.deleteIfExists(p)
                    }
                }
            return true
        } catch (_: Exception) {
            // Hint to the JVM to release file handles and wait a bit before retrying
            System.gc()
            Thread.sleep(delayMs)
        }
    }

    return false
}