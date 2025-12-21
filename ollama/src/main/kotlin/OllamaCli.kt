package org.ivcode.ai.ollama

import com.fasterxml.jackson.databind.DeserializationFeature
import io.github.ollama4j.utils.Utils
import org.ivcode.ai.ollama.agent.OllamaChatAgent
import org.ivcode.ai.ollama.agent.OllamaChatAgentFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.WebApplicationType
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.SpringApplication
import org.springframework.context.annotation.Bean


const val CLI_SYSTEM_MESSAGE =
"""
You are an AI assistant that follows the user’s instructions first. If a request is not explicitly about formatting,
present your answer in plain text suitable for a terminal (no markdown, no code fences). Only apply special formatting
when the tester explicitly asks for it. Keep responses clear, concise, and respectful.
"""

@SpringBootApplication
internal class OllamaCli {

    @Bean
    fun runner(agentFactory: OllamaChatAgentFactory) = CommandLineRunner { args ->
        val session = agentFactory.createOllamaSession()
        session.systemMessages.add { CLI_SYSTEM_MESSAGE }
        session.startChat()
    }

    fun OllamaChatAgent.startChat() {

        // Main REPL loop: read user input, send a chat request including history, stream response.
        while (true) {
            print("User: ")
            val userInput = readlnOrNull()?.trim()
            // Exit on EOF or the literal "exit" (case-insensitive)
            if (userInput == null || userInput.equals("exit", ignoreCase = true)) break
            // Ignore empty lines (no-op)
            if (userInput.isEmpty()) continue

            print("Assistant: ")
            chat(userInput) { resp ->
                val chunk = resp.message?.response
                if (!chunk.isNullOrEmpty()) {
                    print(chunk)
                }

                val done = resp.isDone
                if (done) {
                    println()
                }
            }
        }

        // Clean exit message when the REPL loop ends.
        println("Goodbye.")
    }
}

internal fun main(): Unit = startCli()

fun startCli() {
    Utils.getObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)

    val app = SpringApplication(OllamaCli::class.java)
    app.webApplicationType = WebApplicationType.NONE // ensures no web server
    app.run()
}
