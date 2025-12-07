package org.ivcode.beeboop.pluginman

class PluginFactory {
    fun <T> createInstance(type: PluginType<T>, iml: Class<*>): T {
        if(!iml.isAssignableFrom(type.instanceOf)) {
            throw IllegalArgumentException("Class ${iml.name} is not of type ${type.text}")
        }

        @Suppress("UNCHECKED_CAST")
        val typedImpl = iml as Class<out T>


        return type.factory(typedImpl)
    }
}