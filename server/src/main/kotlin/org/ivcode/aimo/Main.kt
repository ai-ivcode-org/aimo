package org.ivcode.aimo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

const val PROPERTY_PREFIX_APP_NAME = "aimo"

@SpringBootApplication
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
