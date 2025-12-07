package org.ivcode.beeboop.plugin.chat;

public record ChatMessage (
    Long id,
    Long timestamp,
    Role role,
    String response,
    String thinking,
    String toolName
){
    enum Role {
        SYSTEM,
        USER,
        ASSISTANT,
        TOOL,
        ERROR
    }
}
