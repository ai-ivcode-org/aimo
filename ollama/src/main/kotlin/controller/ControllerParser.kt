package org.ivcode.ai.ollama.controller

import org.ivcode.ai.ollama.annotations.SystemMessage
import org.ivcode.ai.ollama.system.KFunctionSystemMessage
import org.ivcode.ai.ollama.system.KPropertySystemMessage
import org.ivcode.ai.ollama.system.OllamaSystemMessage
import kotlin.reflect.KProperty
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.full.declaredMemberProperties

class ControllerParser {

    fun parseController(controller: Any) = mutableListOf<OllamaSystemMessage>().apply {
        addAll(createSystemMessageFromProperties(controller))
        addAll(createSystemMessageFromFunctions(controller))
    }.toList()

    private fun createSystemMessageFromProperties(controller: Any): List<KPropertySystemMessage> {
        val properties = controller::class.declaredMemberProperties.filter { property ->
            property.annotations.any { annotation ->
                annotation.annotationClass == SystemMessage::class
            }
        }

        return properties.map { property -> KPropertySystemMessage(
            instance = controller,
            property = property as KProperty<*>
        )}
    }

    private fun createSystemMessageFromFunctions(controller: Any): List<OllamaSystemMessage> {
        // find system message functions
        val functions = controller::class.declaredMemberFunctions.filter { function ->
            function.annotations.any { annotation ->
                annotation.annotationClass == SystemMessage::class
            }
        }

        // Validate each function
        for (function in functions) {
            // A system message function must have no parameters.
            if (function.parameters.size != 1) {
                throw IllegalArgumentException("System message function ${function.name} must have no parameters.")
            }

            // A system message function must return an object.
            if (function.returnType.classifier == Unit::class || function.returnType.classifier == Nothing::class) {
                throw IllegalArgumentException("System message function ${function.name} must return a value.")
            }
        }

        // Create SystemMessage instances
        return functions.map { function -> KFunctionSystemMessage(
            instance = controller,
            function = function
        )}
    }
}

