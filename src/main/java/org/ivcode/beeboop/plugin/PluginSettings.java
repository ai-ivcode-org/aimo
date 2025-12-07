package org.ivcode.beeboop.plugin;

import java.util.Map;

public record PluginSettings(
    Map<String, Object> adminSettings
) {}
