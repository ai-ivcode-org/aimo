package org.ivcode.beeboop.plugin.tool;

import java.util.List;

public record ToolPluginModel (
    List<String> staticSystemMessages,
    List<SystemMessage> dynamicSystemMessages,
    List<Tool> tools
){}
