package org.ivcode.beeboop.plugin.chat;

@FunctionalInterface
public interface ChatCallback {
    void onMessage(ChatMessage message);
}
