package org.ivcode.common.data.storage

import java.io.InputStream
import org.ivcode.common.data.exception.AlreadyExistsException

/**
 * Abstract storage interface for reading and writing data streams.
 *
 * Implementations back this interface with a concrete storage mechanism
 * (filesystem, object store, in-memory map, etc.). Paths are treated as
 * textual identifiers interpreted by the implementation; callers should treat
 * paths as opaque strings whose semantics (separator character, normalization,
 * whether they represent directories or files) are defined by the concrete
 * implementation.
 */
interface Storage {

    /**
     * Open an input stream to read the resource at the given path.
     *
     * Implementations should return an open [InputStream] when the resource
     * exists and is readable, or null when the resource is absent. The caller
     * is responsible for closing the returned stream. Implementations may
     * throw runtime exceptions for underlying I/O or permission errors.
     *
     * @param path textual path or identifier for the resource to read
     * @return an open [InputStream] for the resource, or null if the resource
     *         does not exist
     */
    fun read(path: String): InputStream?

    /**
     * Creates a new entry, writing data from the provided [InputStream] to the
     * resource identified by [path].
     *
     * Implementations are expected to consume and close the provided stream. They
     * may throw runtime exceptions for underlying I/O or permission errors.
     *
     * @param path textual path or identifier where the data should be written
     * @param data input stream containing the data to write
     *
     * @throws AlreadyExistsException if a resource already exists at the specified path
     */
    fun create(path: String, data: InputStream)

    /**
     * Replaces an existing resource identified by [path] with data from the
     * provided [InputStream].
     *
     * Implementations are expected to consume and close the provided stream. They
     * may throw runtime exceptions for underlying I/O or permission errors.
     *
     * @param path textual path or identifier of the resource to update
     * @param data input stream containing the new data
     */
    fun update(path: String, data: InputStream)

    /**
     * Creates or replaces the resource identified by [path] with data from
     * the provided [InputStream].
     *
     * Implementations are expected to consume and close the provided stream. They
     * may throw runtime exceptions for underlying I/O or permission errors.
     *
     * @param path textual path or identifier of the resource to create or update
     * @param data input stream containing the data
     */
    fun upsert(path: String, data: InputStream)

    /**
     * Delete the resource identified by [path]. If the resource does not exist
     * the operation should be a no-op. Implementations may throw runtime
     * exceptions for permission or I/O failures.
     *
     * @param path textual path or identifier of the resource to remove
     */
    fun delete(path: String)

    /**
     * List resource paths under the directory identified by [parentPath]. The
     * meaning of "under" (recursive vs non-recursive), the separator character,
     * and whether the returned paths are relative or absolute are defined by the
     * concrete implementation; callers should consult the specific
     * implementation's documentation for exact semantics.
     *
     * Implementations should return an empty list when the directory does not
     * exist or contains no matching entries.
     *
     * @param parentPath textual path or identifier representing the directory to list
     * @return list of resource path strings as defined by the implementation
     */
    fun listPaths(parentPath: String = ""): List<String>

    fun exists(path: String): Boolean
}