package org.ivcode.aimo.pluginmanager.loader

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.io.InputStream
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.ConcurrentHashMap

class PluginClassloaderManager(
    private val directory: Path,
    private val mapper: ObjectMapper = jacksonObjectMapper()
): AutoCloseable {

    private val classLoaders = ConcurrentHashMap<String, PluginClassloader>()

    fun get(
        id: String,
        fallback: (id: String) -> InputStream
    ): PluginClassloader = classLoaders.computeIfAbsent(id) {
        val url = fallback(id).use { input ->
            savePluginPackage(id, input)
        }

        PluginClassloader(url, mapper)
    }

    private fun savePluginPackage (
        id: String,
        input: InputStream,
    ): URL {
        // Ensure the target directory exists
        Files.createDirectories(directory)
        val dest = directory.resolve("$id.jar")

        // Stream the input to the destination file
        Files.newOutputStream(dest).use { out ->
            input.copyTo(out)
        }

        return dest.toUri().toURL()
    }

    override fun close() {
        classLoaders.values.forEach { it.close() }
    }
}