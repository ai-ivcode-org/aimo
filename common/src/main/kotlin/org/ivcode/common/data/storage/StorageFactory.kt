package org.ivcode.common.data.storage

import org.ivcode.common.PROPERTY_PREFIX_APP_NAME

const val PROPERTY_NAME_STORAGE_TYPE = "$PROPERTY_PREFIX_APP_NAME.storage.type"

interface StorageFactory {
    fun createStorage(basePath: String): Storage
}