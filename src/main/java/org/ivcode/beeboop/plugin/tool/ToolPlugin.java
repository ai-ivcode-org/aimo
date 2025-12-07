package org.ivcode.beeboop.plugin.tool;

import org.ivcode.beeboop.plugin.Plugin;
import org.ivcode.beeboop.plugin.PluginSettings;

public interface ToolPlugin extends Plugin {
    ToolPluginModel createToolModel(PluginSettings settings);
}
