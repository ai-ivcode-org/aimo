package org.ivcode.test.sysinfo

import org.ivcode.aimo.plugin.tool.ToolEntryPoint
import org.ivcode.aimo.plugin.tool.ToolPlugin

class SystemInfoEntryPoint: ToolEntryPoint {
    override fun configure(): ToolPlugin  = ToolPlugin.Builder(
        id = "sysinfo",
        name = "System Info Tool",
        description = "Provides system information about the AI environment"
    )
        .withSystemMessage("this is a system message from the system info tool")
        .build()
}