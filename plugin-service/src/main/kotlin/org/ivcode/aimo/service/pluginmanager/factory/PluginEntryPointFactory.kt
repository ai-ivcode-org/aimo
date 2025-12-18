package org.ivcode.aimo.service.pluginmanager.factory

import org.ivcode.aimo.plugin.PluginEntryPoint
import org.springframework.stereotype.Component

/**
 * Factory responsible for instantiating a plugin's entry point class.
 *
 * The factory encapsulates the creation logic for `PluginEntryPoint` instances.
 */
@Component
internal class PluginEntryPointFactory {

    /**
     * Create a new instance of the provided plugin entry point implementation class.
     *
     * @param iml the implementation class that implements [org.ivcode.aimo.plugin.PluginEntryPoint].
     *            The factory will instantiate this class using its no-argument constructor.
     * @return a new [org.ivcode.aimo.plugin.PluginEntryPoint] instance created from the given class.
     * @throws ReflectiveOperationException if instantiation fails (e.g. no accessible no-arg constructor,
     *         constructor throws, or other reflection-related errors). Callers should handle or wrap this as needed.
     */
    fun createPluginEntryPoint(iml: Class<out PluginEntryPoint>): PluginEntryPoint {
        // TODO add support for a more complex instantiation process
        return iml.getDeclaredConstructor().newInstance()
    }

}