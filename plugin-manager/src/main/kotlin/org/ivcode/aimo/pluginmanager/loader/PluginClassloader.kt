package org.ivcode.aimo.pluginmanager.loader

import com.fasterxml.jackson.databind.ObjectMapper
import org.ivcode.aimo.plugin.PluginEntryPoint
import org.ivcode.common.PROPERTY_PREFIX_APP_NAME
import java.net.JarURLConnection
import java.net.URL
import java.nio.file.Paths
import java.util.jar.JarFile
import java.util.jar.JarInputStream

const val PLUGIN_METADATA_FILE = "META-INF/$PROPERTY_PREFIX_APP_NAME/plugin.json"

/**
 * A classloader for loading in a plugin. Each plugin gets its own classloader to isolate dependencies. Each plugin
 * should, for the most part, run as an independent application within the host application. It will only share
 * java classes and classes included in the plugin api. It's assumed the package will completely contained, including
 * all dependencies. It should be a fat-jar or uber-jar.
 */
class PluginClassloader (
    url: URL,
    private val mapper: ObjectMapper,
): ParentLimitedClassloader (
    urls = arrayOf(url),
    allowedParentPatterns = setOf (
        "org.ivcode.aimo.plugin.*",
        "org.ivcode.aimo.common.*",
    )
) {
    val metadata: PluginMeta = getPluginMetadata(url)

    fun loadPluginClass(): Class<out PluginEntryPoint> {
        val impl = this.loadClass(metadata.entryPoint)
        if(!PluginEntryPoint::class.java.isAssignableFrom(impl)) {
            throw IllegalArgumentException("Class ${impl.name} is not of type ${PluginEntryPoint::class.java.name}")
        }

        @Suppress("UNCHECKED_CAST")
        return impl as Class<out PluginEntryPoint>
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
}