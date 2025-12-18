package org.ivcode.aimo.service.pluginmanager.classloader

import org.ivcode.aimo.plugin.PluginEntryPoint
import org.ivcode.aimo.service.pluginmanager.model.Metadata
import java.net.URL


/**
 * The set of packages the plugin classloader can pull from the parent classloader.
 */
private val ALLOWED_PACKAGES = setOf (
    "org.ivcode.aimo.plugin.*",
    "org.ivcode.aimo.common.*",
)


/**
 * A classloader for loading in a plugin. Each plugin gets its own classloader to isolate dependencies. Each plugin
 * should, for the most part, run as an independent application within the host application. It will only share
 * java classes and classes included in the plugin api. It's assumed the package will completely contained, including
 * all dependencies. It should be a fat-jar or uber-jar.
 */
internal class PluginClassloader (
    url: URL,
    val metadata: Metadata,
    allowedPackages: Set<String> = emptySet(),
): ParentLimitedClassloader(
    urls = arrayOf(url),
    allowedParentPatterns = ALLOWED_PACKAGES + allowedPackages
) {
    fun loadPluginClass(): Class<out PluginEntryPoint> {
        val impl = this.loadClass(metadata.entryPoint)
        if(!PluginEntryPoint::class.java.isAssignableFrom(impl)) {
            throw IllegalArgumentException("Class ${impl.name} is not of type ${PluginEntryPoint::class.java.name}")
        }

        @Suppress("UNCHECKED_CAST")
        return impl as Class<out PluginEntryPoint>
    }
}