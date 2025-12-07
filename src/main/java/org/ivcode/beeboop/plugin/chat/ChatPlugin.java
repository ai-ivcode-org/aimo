package org.ivcode.beeboop.plugin.chat;

import org.ivcode.beeboop.plugin.Plugin;
import org.ivcode.beeboop.plugin.PluginSettings;

public interface ChatPlugin extends Plugin {
    ChatModel createChatModel(PluginSettings config);
}
