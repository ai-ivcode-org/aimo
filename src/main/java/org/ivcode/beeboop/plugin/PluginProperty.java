package org.ivcode.beeboop.plugin;

import java.util.List;

// A UI property for configuring the plugin
public record PluginProperty (
        String id,
        String description,
        boolean isRequired,
        List<String> options
) {}

