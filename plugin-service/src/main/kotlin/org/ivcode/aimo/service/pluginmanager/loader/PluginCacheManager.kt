package org.ivcode.aimo.service.pluginmanager.loader

import com.fasterxml.jackson.databind.ObjectMapper
import org.ivcode.aimo.service.pluginmanager.PLUGIN_CACHE_PATH
import org.ivcode.aimo.service.pluginmanager.model.PackageInfo
import org.ivcode.aimo.service.pluginmanager.model.Metadata
import org.ivcode.common.PROPERTY_PREFIX_APP_NAME
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import java.io.InputStream
import java.net.JarURLConnection
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.nio.file.StandardOpenOption
import java.util.jar.JarFile
import java.util.jar.JarInputStream

private const val PLUGIN_METADATA_FILE = "META-INF/$PROPERTY_PREFIX_APP_NAME/plugin.json"

@Component
internal class PluginCacheManager (
    @param:Qualifier(PLUGIN_CACHE_PATH) private val root: Path,
    @Suppress("SpringJavaInjectionPointsAutowiringInspection") private val mapper: ObjectMapper
) {

    companion object {
        fun getPluginMetadata(url: URL, mapper: ObjectMapper): Metadata {
            try {
                val entryName = PLUGIN_METADATA_FILE.trimStart('/')

                // Fast path: file-system backed jar URL -> open with JarFile
                if (url.protocol == "file") {
                    val jarPath = Paths.get(url.toURI())
                    JarFile(jarPath.toFile()).use { jar ->
                        val entry = jar.getJarEntry(entryName)
                            ?: throw IllegalArgumentException("Plugin metadata '$entryName' not found in $url")

                        jar.getInputStream(entry).use { input ->
                            return mapper.readValue(input, Metadata::class.java)
                        }
                    }
                }

                // If it's a jar: URL, extract the nested file URL
                if (url.protocol == "jar") {
                    val conn = url.openConnection()
                    if (conn is JarURLConnection) {
                        JarFile(conn.jarFileURL.toURI().let { Paths.get(it).toFile() }).use { jar ->
                            val entry = jar.getJarEntry(entryName)
                                ?: throw IllegalArgumentException("Plugin metadata '$entryName' not found in $url")

                            jar.getInputStream(entry).use { input ->
                                return mapper.readValue(input, Metadata::class.java)
                            }
                        }
                    }
                }

                // Generic fallback: open stream and iterate with JarInputStream (works for remote URLs too)
                url.openStream().use { stream ->
                    JarInputStream(stream).use { jis ->
                        var entry = jis.nextJarEntry
                        while (entry != null) {
                            if (!entry.isDirectory && entry.name == entryName) {
                                return mapper.readValue(jis, Metadata::class.java)
                            }
                            jis.closeEntry()
                            entry = jis.nextJarEntry
                        }
                    }
                }

                throw IllegalArgumentException("Plugin metadata '$entryName' not found in $url")

            } catch (e: IllegalArgumentException) {
                throw e
            } catch (e: Exception) {
                throw RuntimeException("Failed to read plugin metadata from $url", e)
            }
        }
    }

    fun savePluginPackage (
        input: InputStream,
        overwrite: Boolean = true,
        lazyFilename: (meta: Metadata) -> String,
    ): PackageInfo {
        Files.createDirectories(root)
        val tmp = Files.createTempFile(root,null, ".tmp")

        try {
            Files.newOutputStream(tmp, StandardOpenOption.TRUNCATE_EXISTING).use {
                input.copyTo(it)
            }

            val tmpUrl = tmp.toUri().toURL()
            val meta: Metadata = getPluginMetadata(tmpUrl, mapper)

            val filename = lazyFilename(meta)
            val target = getPath(filename)

            if(Files.exists(target) && !overwrite) {
                throw IllegalArgumentException("Plugin package already exists: $filename")
            }

            try {
                // Try atomic move first
                Files.move(tmp, target, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING)
            } catch (_: java.nio.file.AtomicMoveNotSupportedException) {
                // Fallback to non-atomic replace
                Files.move(tmp, target, StandardCopyOption.REPLACE_EXISTING)
            }

            return PackageInfo(
                metadata =  meta,
                url = target.toUri().toURL()
            )
        } finally {
            Files.deleteIfExists(tmp)
        }
    }

    fun savePluginPackage (
        filename: String,
        input: InputStream,
        overwrite: Boolean = true
    ): PackageInfo {
        val file = getPath(filename)
        Files.createDirectories(root)

        val openOptions = if (overwrite) {
            // Create or replace existing file
            arrayOf(StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)
        } else {
            // Atomically create a new file and fail if it already exists.
            arrayOf(StandardOpenOption.CREATE_NEW)
        }

        Files.newOutputStream(file, *openOptions).use { out ->
            input.copyTo(out)
        }

        val url = file.toUri().toURL()
        val meta: Metadata = getPluginMetadata(url, mapper)

        return PackageInfo(
            metadata = meta,
            url = url
        )
    }

    fun getPath (
        filename: String,
    ): Path {
        // TODO make sure the filename is valid

        return root.resolve(filename)
    }

    fun getUrl(filename: String): URL = getPath(filename).toUri().toURL()

    fun getPluginPackage (filename: String): InputStream = getUrl(filename).openStream()

    fun getPluginMetadata(filename: String): Metadata {
        val url = getUrl(filename)
        return getPluginMetadata(url, mapper)
    }

    fun listPackagePaths(): Set<String> {
        if (!Files.exists(root)) return emptySet()

        val names = mutableSetOf<String>()
        try {
            Files.newDirectoryStream(root).use { stream ->
                for (p in stream) {
                    if (Files.isRegularFile(p)) {
                        names.add(p.fileName.toString())
                    }
                }
            }
        } catch (e: Exception) {
            throw RuntimeException("Failed to list plugin files in $root", e)
        }

        return names
    }

    /**
     * Delete a plugin package file under the managed `root` directory.
     *
     * The method resolves `packagePath` to an absolute path via [getPath] and deletes the file.
     *
     * @param packagePath the relative filename or path of the package to delete (resolved by `getPath`)
     * @param ifExists when `true`, the deletion will be performed only if the file exists
     *                 (no exception is thrown if the file is missing). When `false`, an attempt
     *                 to delete a non-existent file will throw a `java.nio.file.NoSuchFileException`.
     *
     * @throws java.nio.file.NoSuchFileException if `ifExists` is `false` and the target does not exist
     * @throws java.io.IOException for other I/O errors encountered while deleting
     */
    fun deletePackage(packagePath: String, ifExists: Boolean = false) {
        val path = getPath(packagePath)
        if(ifExists) {
            Files.deleteIfExists(path)
        } else {
            Files.delete(path)
        }
    }
}