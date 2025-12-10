package org.ivcode.aimo.utils

import org.ivcode.common.data.storage.Storage
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Paths

class FileStorge: Storage {

    var basePath: String? = null

    override fun read(path: String): InputStream? {
        val p = try {
            val bp = basePath
            if (bp != null) {
                Paths.get(bp).resolve(path).normalize()
            } else {
                Paths.get(path).normalize()
            }
        } catch (_: Exception) {
            return null
        }

        return try {
            if (Files.exists(p) && Files.isRegularFile(p) && Files.isReadable(p)) {
                Files.newInputStream(p)
            } else {
                null
            }
        } catch (_: Exception) {
            null
        }
    }

    override fun write(path: String, data: InputStream) {
        throw UnsupportedOperationException("read-only")
    }

    override fun delete(path: String) {
        throw UnsupportedOperationException("read-only")
    }

    override fun listPaths(parentPath: String): List<String> {
        throw UnsupportedOperationException()
    }
}