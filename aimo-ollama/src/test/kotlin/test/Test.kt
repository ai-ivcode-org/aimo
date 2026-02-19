package test

import org.ivcode.ai.ollama.client.ChatCallback
import org.ivcode.ai.ollama.client.ChatRequest
import org.ivcode.ai.ollama.client.ChatResponse
import org.ivcode.ai.ollama.client.Function
import org.ivcode.ai.ollama.client.Message
import org.ivcode.ai.ollama.client.OllamaChatClient
import org.ivcode.ai.ollama.client.Parameters
import org.ivcode.ai.ollama.client.Property
import org.ivcode.ai.ollama.client.Tool
import org.ivcode.ai.ollama.client.Type
import org.junit.jupiter.api.Test

class Test {


    @Test
    fun testClient() {
        val client = OllamaChatClient()

        val request = ChatRequest(
            model = "qwen3:8b",
            messages = listOf(
                Message(
                    role = "user",
                    content = "What is the temperature in New York?"
                )
            ),
            stream = true,
            tools = listOf(
                Tool(
                    function = Function(
                        name = "get_temperature",
                        description = "Get the current temperature for a city",
                        parameters = Parameters(
                            required = listOf("city"),
                            properties = mapOf(
                                "city" to Property(
                                    type = Type.STRING,
                                    description = "The name of the city"
                                )
                            )
                        )
                    )
                )
            )
        )

        val response = client.chat(request, object: ChatCallback {

            override fun onResponse(response: ChatResponse) {
                println("Received message: $response")
            }

            override fun onComplete() {
                println("Chat complete")
            }

            override fun onError(error: Throwable) {
                println("Error: ${error.message}")
            }
        })

        println(response)
    }
}