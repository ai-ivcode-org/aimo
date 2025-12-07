package org.ivcode.beeboop.plugin.chat;

import org.ivcode.beeboop.plugin.tool.SystemMessage;
import org.ivcode.beeboop.plugin.tool.Tool;

import java.util.List;

public record ChatRequest(
        boolean stream,
        List<String> staticSystemMessages,
        List<SystemMessage> dynamicSystemMessages,
        List<ChatMessage> messages,
        List<Tool> tools
) {}
