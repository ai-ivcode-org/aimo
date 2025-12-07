package org.ivcode.beeboop.plugin.tool;

import java.util.List;
import java.util.Map;

public interface Tool {

    String getName();
    String getDescription();
    List<ToolParameter> getParameters();
    String execute(Map<String, Object> params);

    record ToolParameter(
        String name,
        String description,
        ToolParameterType type,
        boolean isRequired
    ) {}

    enum ToolParameterType {
        STRING,
        INTEGER,
        NUMBER,
        BOOLEAN,
    }
}