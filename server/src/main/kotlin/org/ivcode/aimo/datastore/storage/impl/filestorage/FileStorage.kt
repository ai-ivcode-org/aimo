package org.ivcode.aimo.datastore.storage.impl.filestorage

import org.ivcode.common.data.storage.Storage
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.stream.Collectors

/**
 * Simple filesystem-backed implementation of [Storage].
 *
 * All operations are rooted at [basePath]. Paths provided to the API are treated
 * as relative to [basePath]; path traversal attempts ("..") that would escape
 * the base directory are rejected with [IllegalArgumentException].
 *
 */
class FileStorage(
    private val basePath: Path
) : Storage {

    private val baseDir: Path = basePath.toAbsolutePath().normalize()

    /**
     * Resolve a user-supplied relative path against the configured base
     * directory and normalize the result.
     *
     * This method enforces that the resolved path remains under [baseDir]. If
     * the resolved path would escape the base directory an
     * [IllegalArgumentException] is thrown.
     *
     * @param path a relative or simple path (may contain separators or '..')
     * @return the normalized absolute [Path] within [baseDir]
     * @throws IllegalArgumentException if the resolved path escapes [baseDir]
     */
    private fun resolveRelative(path: String): Path {
        val relative = Paths.get(path).normalize()
        val resolved = baseDir.resolve(relative).normalize()
        if (!resolved.startsWith(baseDir)) {
            throw IllegalArgumentException("Invalid path: escapes base directory")
        }
        return resolved
    }

    /**
     * Open and return an [InputStream] for the file at the given relative
     * path.
     *
     * The caller is responsible for closing the returned stream. If the file
     * does not exist or is not a regular file this method returns null.
     *
     * @param path relative path to the file (treated relative to [basePath])
     * @return an open [InputStream] if the file exists; otherwise null
     * @throws IllegalArgumentException if the path escapes [baseDir]
     */
    override fun read(path: String): InputStream? {
        val target = resolveRelative(path)
        return if (Files.exists(target) && Files.isRegularFile(target)) {
            Files.newInputStream(target)
        } else {
            null
        }
    }

    /**
     * Write the provided input stream to the target relative path. Parent
     * directories will be created if necessary. Existing files at the target
     * path will be replaced.
     *
     * The provided [InputStream] will be closed by this method (successful or
     * not). Any IOExceptions thrown by the underlying file system will be
     * propagated to the caller.
     *
     * @param path relative path where the data should be written
     * @param data input stream containing the data to write (closed by this method)
     * @throws IllegalArgumentException if the path escapes [baseDir]
     */
    override fun write(path: String, data: InputStream) {
        val target = resolveRelative(path)
        try {
            // Ensure parent directories exist
            target.parent?.let { Files.createDirectories(it) }
            // Copy stream to file, replacing existing
            Files.copy(data, target, StandardCopyOption.REPLACE_EXISTING)
        } finally {
            try {
                data.close()
            } catch (_: Exception) {
                // ignore close failures
            }
        }
    }

    /**
     * Delete the file at the given relative path if it exists.
     *
     * This method does nothing if the file is already absent. Any IO
     * exceptions from the file system are propagated to the caller.
     *
     * @param path relative path to delete
     * @throws IllegalArgumentException if the path escapes [baseDir]
     */
    override fun delete(path: String) {
        val target = resolveRelative(path)
        try {
            Files.deleteIfExists(target)
        } catch (ex: Exception) {
            // propagate as runtime exception to caller
            throw ex
        }
    }

    /**
     * List files under the directory specified by [parentPath] (relative to
     * [basePath]). The returned list contains file paths relative to the
     * configured [baseDir], using '/' as the separator.
     *
     * If [parentPath] is empty the listing starts at the [baseDir]. If the
     * resolved directory does not exist or is not a directory an empty list is
     * returned. Path traversal attempts are rejected.
     *
     * @param parentPath relative directory path to list (empty = base directory)
     * @return list of relative file paths under the resolved directory
     * @throws IllegalArgumentException if the resolved directory escapes [baseDir]
     */
    override fun listPaths(parentPath: String): List<String> {
        if (!Files.exists(baseDir) || !Files.isDirectory(baseDir)) return emptyList()

        // Treat prefix as a directory path relative to baseDir. Normalize it and
        // resolve against baseDir to protect from traversal.
        val normalizedPrefix = parentPath
            .trim()
            .replace('\\', '/')
            .trim()
            .trimStart('/')
            .trimEnd('/')

        val startDir: Path = if (normalizedPrefix.isEmpty()) {
            baseDir
        } else {
            // resolveRelative will throw if the resolved path escapes baseDir
            val resolved = resolveRelative(normalizedPrefix)
            if (!Files.exists(resolved) || !Files.isDirectory(resolved)) {
                return emptyList()
            }
            resolved
        }

        Files.walk(startDir).use { stream ->
            return stream
                .filter { Files.isRegularFile(it) }
                .map { baseDir.relativize(it).toString().replace('\\', '/') }
                .collect(Collectors.toList())
        }
    }
}