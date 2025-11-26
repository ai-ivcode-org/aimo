package org.ivcode.beeboop.persistence.filesystem

import java.io.InputStream

/**
 * A simple file system interface for file operations.
 *
 * This is an abstraction layer so that the implementation may scale from local file system to distributed file systems
 * as needed.
 */
interface FileSystem {
    fun getFile(path: String): InputStream
    fun writeFile(path: String, content: InputStream)
}