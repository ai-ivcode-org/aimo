package org.ivcode.aimo.service.pluginmanager.utils

import org.ivcode.aimo.service.pluginmanager.model.Metadata

/**
 * Converts plugin metadata to a standard filename
 */
internal fun toFilename(metadata: Metadata): String = "${metadata.id}${if(metadata.version!=null) "-"+metadata.version else "" }.jar"