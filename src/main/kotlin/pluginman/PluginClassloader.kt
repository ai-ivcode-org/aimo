package org.ivcode.beeboop.pluginman

import com.fasterxml.jackson.databind.ObjectMapper
import org.ivcode.beeboop.PROPERTY_PREFIX_APP_NAME
import org.ivcode.beeboop.plugin.Plugin
import java.io.InputStream
import java.net.JarURLConnection
import java.net.URL
import java.net.URLClassLoader
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.jar.JarFile
import java.util.jar.JarInputStream

const val PLUGIN_METADATA_FILE = "META-INF/$PROPERTY_PREFIX_APP_NAME/plugin.json"

/**
 * Custom classloader for loading plugins and getting their metadata.
 *
 * @param directory The directory where the plugin is saved on disk.
 */
class PluginClassloader (
    name: String,
    private val directory: Path,
    private val mapper: ObjectMapper,
    urls: Array<URL> = arrayOf(),
    parent: ClassLoader = getSystemClassLoader(),
 ): URLClassLoader(
     name,
     urls,
     parent
 ) {

    // Generic storage: map from PluginType -> (map of pluginId -> PluginMeta)
    private val plugins: MutableMap<PluginType<*>, MutableMap<String, PluginMeta>> =
        PluginType.ALL_TYPES.associateWith { mutableMapOf<String, PluginMeta>() }.toMutableMap()

    init {
        val entryName = PLUGIN_METADATA_FILE.trimStart('/')

        val resources = parent.getResources(entryName)
        while (resources.hasMoreElements()) {
            resources.nextElement().openStream().use { stream ->
                val metadata = mapper.readValue(stream, PluginMeta::class.java)
                val type = typeOf(metadata)
                plugins[type]?.put(metadata.id, metadata)
            }
        }
    }


    fun getPlugins(type: PluginType<Any>) = plugins[type]?.keys ?: emptySet()

    fun getPluginMetadata(type: PluginType<*>, id: String): PluginMeta? = plugins[type]?.get(id)


    fun addPluginPackage(name: String, input: InputStream): PluginMeta {
        val url = addPluginToClassloader(name, input)
        val metadata = getPluginMetadata(url)
        val type = typeOf(metadata)
        plugins[type]?.put(metadata.id, metadata)

        return metadata
    }

    private fun addPluginToClassloader(name: String, input: InputStream): URL {
        // Ensure the target directory exists
        Files.createDirectories(directory)
        val dest = directory.resolve("$name.jar")
        // Stream the input to the destination file
        Files.newOutputStream(dest).use { out ->
            input.copyTo(out)
        }

        val url = dest.toUri().toURL()
        // Add the new file URL to the classloader
        this.addURL(url)

        return url
    }

    private fun getPluginMetadata(url: URL): PluginMeta {
        try {
            val entryName = PLUGIN_METADATA_FILE.trimStart('/')

            // Fast path: file-system backed jar URL -> open with JarFile
            if (url.protocol == "file") {
                val jarPath = Paths.get(url.toURI())
                JarFile(jarPath.toFile()).use { jar ->
                    val entry = jar.getJarEntry(entryName)
                        ?: throw IllegalArgumentException("Plugin metadata '$entryName' not found in $url")

                    jar.getInputStream(entry).use { input ->
                        return mapper.readValue(input, PluginMeta::class.java)
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
                            return mapper.readValue(input, PluginMeta::class.java)
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
                            return mapper.readValue(jis, PluginMeta::class.java)
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

    private fun typeOf(meta: PluginMeta): PluginType<*> {
        val clazz = this.loadClass(meta.entryPoint)

        // Ensure the entry point is a concrete class (not an interface or abstract)
        if (clazz.isInterface || java.lang.reflect.Modifier.isAbstract(clazz.modifiers)) {
            throw IllegalArgumentException("Plugin entry point '${meta.entryPoint}' must be a concrete, non-abstract class")
        }

        val matches = PluginType.ALL_TYPES.filter { it.instanceOf.isAssignableFrom(clazz) }

        return when (matches.size) {
            1 -> matches.first()
            0 -> throw IllegalArgumentException("Plugin entry point '${meta.entryPoint}' does not implement a known plugin interface")
            else -> throw IllegalArgumentException("Plugin entry point '${meta.entryPoint}' matches multiple plugin interfaces: ${matches.joinToString(", ") { it.text }}")
        }
    }
}

data class PluginMeta (
    val id: String,
    val name: String,
    val description: String?,
    val version: String?,
    val entryPoint: String,
)