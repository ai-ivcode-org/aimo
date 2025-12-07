package org.ivcode.beeboop.utils

import java.nio.file.Path

/**
 * Returns true if this path is an ancestor (parent) of [other].
 * The check normalizes and converts both paths to absolute form before comparing.
 * A path is not considered a parent of itself.
 */
fun Path.isParentOf(other: Path): Boolean {
    val a = this.toAbsolutePath().normalize()
    val b = other.toAbsolutePath().normalize()
    return a != b && b.startsWith(a)
}

/**
 * Validates that `this` is not a parent/ancestor of [basePath].
 * Throws IllegalArgumentException if the check fails.
 */
fun Path.validateNotParent(basePath: Path) {
    if (this.isParentOf(basePath)) {
        val a = this.toAbsolutePath().normalize()
        val b = basePath.toAbsolutePath().normalize()
        throw IllegalArgumentException("Path '$a' must not be a parent of '$b'")
    }
}