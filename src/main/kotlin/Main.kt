package org.ivcode.beeboop

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

const val PROPERTY_PREFIX_APP_NAME = "beeboop"

@SpringBootApplication
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
