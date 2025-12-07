package org.ivcode.beeboop.plugin.chat;

import java.util.List;

public interface ChatModel {
    List<ChatMessage> chat(
            ChatRequest request,
            ChatCallback callback
    );
}
