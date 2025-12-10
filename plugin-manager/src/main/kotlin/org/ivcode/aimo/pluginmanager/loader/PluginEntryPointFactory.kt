package org.ivcode.aimo.pluginmanager.loader

import org.ivcode.aimo.plugin.PluginEntryPoint
import org.springframework.stereotype.Component

@Component
class PluginEntryPointFactory {
    fun createPluginEntryPoint(iml: Class<out PluginEntryPoint>): PluginEntryPoint {
        // TODO add support for a more complex instantiation process
        return iml.getDeclaredConstructor().newInstance()
    }

}