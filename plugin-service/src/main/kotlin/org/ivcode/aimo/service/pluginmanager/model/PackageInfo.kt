package org.ivcode.aimo.service.pluginmanager.model

import java.net.URL

internal data class PackageInfo (
    val metadata: Metadata,
    val url: URL
)