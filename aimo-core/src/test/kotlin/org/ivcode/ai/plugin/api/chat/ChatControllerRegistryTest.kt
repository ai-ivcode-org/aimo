package org.ivcode.ai.plugin.api.chat

import org.ivcode.ai.plugin.api.chat.controller.ChatControllerRegistry
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ChatControllerRegistryTest {

    @Test
    fun `test register controller`() {
        val reg = ChatControllerRegistry()
        reg.register(BasicTestController())

        val entry = reg.getEntry(BasicTestController::class.java.simpleName)!!

        val systemMessageResults = entry.systemMessages.map { it() }.toSet()
        assertEquals(systemMessageResults.size, 2)
        assert(systemMessageResults.contains("System message from BasicTestController"))
        assert(systemMessageResults.contains("System message from function in BasicTestController"))

        val tools = entry.tools
        assertEquals(tools.size, 1)
        val tool = tools.first()
        val echoValue = tool.execute(mapOf("input" to "This is my input"))
        assertEquals("This is my input", echoValue)
    }
}