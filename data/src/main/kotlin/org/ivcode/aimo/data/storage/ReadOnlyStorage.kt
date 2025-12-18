package org.ivcode.aimo.data.storage

import org.ivcode.common.data.storage.Storage
import java.io.InputStream

class ReadOnlyStorage(
    private val storage: Storage,
): Storage {
    override fun read(path: String): InputStream? {
        return storage.read(path)
    }

    override fun create(path: String, data: InputStream) {
        throw UnsupportedOperationException("Create operation is not supported in ReadOnlyFileStorage")
    }

    override fun update(path: String, data: InputStream) {
        throw UnsupportedOperationException("Update operation is not supported in ReadOnlyFileStorage")
    }

    override fun upsert(path: String, data: InputStream) {
        throw UnsupportedOperationException("Upsert operation is not supported in ReadOnlyFileStorage")
    }

    override fun delete(path: String) {
        throw UnsupportedOperationException("Delete operation is not supported in ReadOnlyFileStorage")
    }

    override fun listPaths(parentPath: String): List<String> {
        return storage.listPaths(parentPath)
    }
}