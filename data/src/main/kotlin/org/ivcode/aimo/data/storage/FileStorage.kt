package org.ivcode.aimo.data.storage

import org.ivcode.common.data.exception.AlreadyExistsException
import org.ivcode.common.data.storage.Storage
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.nio.file.StandardOpenOption
import java.nio.file.FileAlreadyExistsException
import java.util.stream.Collectors

/**
 * Simple filesystem-backed implementation of [Storage].
 *
 * All operations are rooted at [basePath]. Paths provided to the API are treated
 * as relative to [basePath]; path traversal attempts ("..") that would escape
 * the base directory are rejected with [IllegalArgumentException].
 *
 */
class FileStorage (
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
    override fun create(path: String, data: InputStream) {
        // Atomically create a new file and fail if it already exists. We create
        // parent directories first, then open an OutputStream with
        // CREATE_NEW which throws FileAlreadyExistsException if target exists.
        data.use { input ->
            val target = resolveRelative(path)
            // Ensure parent directories exist
            target.parent?.let { Files.createDirectories(it) }

            try {
                Files.newOutputStream(target, StandardOpenOption.CREATE_NEW).use { out ->
                    input.copyTo(out)
                }
            } catch (ex: FileAlreadyExistsException) {
                throw AlreadyExistsException("Resource already exists: $path", ex)
            }
        }
    }


    override fun update(path: String, data: InputStream) {
        val target = resolveRelative(path)
        // update must operate on an existing regular file
        if (!Files.exists(target) || !Files.isRegularFile(target)) {
            throw IllegalStateException("Resource does not exist: $path")
        }

        // Ensure parent directories exist (defensive)
        val parent = target.parent ?: baseDir
        Files.createDirectories(parent)

        // Write to a temp file in the same directory then move into place so
        // replacement is atomic (when supported by the filesystem).
        // Revert the per-request unique temp filename changes: remove the UUID import and create the temp file with the original simple prefix.
        val temp = Files.createTempFile(parent, null, ".tmp")
        try {
            Files.newOutputStream(temp, StandardOpenOption.TRUNCATE_EXISTING).use { out ->
                data.copyTo(out)
            }

            try {
                // Try atomic move first
                Files.move(temp, target, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING)
            } catch (_: java.nio.file.AtomicMoveNotSupportedException) {
                // Fallback to non-atomic replace
                Files.move(temp, target, StandardCopyOption.REPLACE_EXISTING)
            }
        } finally {
            // Ensure temp file is removed if something went wrong and it still exists
            try { Files.deleteIfExists(temp) } catch (_: Exception) {}
        }
    }

    override fun upsert(path: String, data: InputStream) {
        val target = resolveRelative(path)

        // Ensure parent directories exist
        val parent = target.parent ?: baseDir
        Files.createDirectories(parent)

        // Write contents to a temp file in the same directory, then move into place
        val temp = Files.createTempFile(parent, "upsert-", ".tmp")
        try {
            Files.newOutputStream(temp, StandardOpenOption.TRUNCATE_EXISTING).use { out ->
                data.copyTo(out)
            }

            try {
                // Prefer atomic move when available
                Files.move(temp, target, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING)
            } catch (_: java.nio.file.AtomicMoveNotSupportedException) {
                // Fallback to non-atomic replace
                Files.move(temp, target, StandardCopyOption.REPLACE_EXISTING)
            }
        } finally {
            // Best-effort cleanup of temp file if something went wrong
            try { Files.deleteIfExists(temp) } catch (_: Exception) {}
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