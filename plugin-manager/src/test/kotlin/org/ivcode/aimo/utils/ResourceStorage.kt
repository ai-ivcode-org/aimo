package org.ivcode.aimo.utils

import org.ivcode.common.data.storage.Storage
import java.io.InputStream

class ResourceStorage(
    private val resourceBasePath: String
): Storage {

    init {
        require(resourceBasePath.isNotBlank())
        require(resourceBasePath.endsWith('/'))
        require(!resourceBasePath.startsWith('/'))
    }

    override fun read(path: String): InputStream? {
        val fullPath = resourceBasePath + path
        return this::class.java.classLoader.getResourceAsStream(fullPath)
    }

    override fun write(path: String, data: InputStream) {
        throw UnsupportedOperationException("ResourceStorage is read-only")
    }

    override fun delete(path: String) {
        throw UnsupportedOperationException("ResourceStorage is read-only")
    }

    override fun listPaths(parentPath: String): List<String> {
        throw UnsupportedOperationException("ResourceStorage does not support listing paths")
    }
}