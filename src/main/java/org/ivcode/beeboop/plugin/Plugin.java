package org.ivcode.beeboop.plugin;

import java.util.List;

public interface Plugin {
    String getId();
    String getName();
    String getDescription();
    List<PluginProperty> adminConfig();
}