package org.ivcode.ai.plugin.api.chat;

import org.ivcode.ai.plugin.api.chat.controller.ChatController;
import org.ivcode.ai.plugin.api.chat.controller.SystemMessage;
import org.ivcode.ai.plugin.api.chat.controller.Tool;

@ChatController
public class JavaChatController {

    @SystemMessage
    String message = "This is my system message from Java!";

    @SystemMessage
    public String sendSystemMessage() {
        return "This is my system message from Java from a method!";
    }

    @Tool (description = "A tool method that should not be used as a system message")
    public String toolMethod() {
        return "This is a tool method and should not be used as a system message.";
    }
}
