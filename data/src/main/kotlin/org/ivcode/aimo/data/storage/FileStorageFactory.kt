package org.ivcode.aimo.data.storage

import org.ivcode.common.data.storage.PROPERTY_NAME_STORAGE_TYPE
import org.ivcode.common.data.storage.Storage
import org.ivcode.common.data.storage.StorageFactory
import org.ivcode.common.utils.validateNotParent
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component
import java.nio.file.Path
import java.nio.file.Paths

@Component
@ConditionalOnProperty(name = [PROPERTY_NAME_STORAGE_TYPE], havingValue = "file", matchIfMissing = true)
class FileStorageFactory(
    private val rootPath: Path = Path.of("./data/storage")
): StorageFactory {

    override fun createStorage(basePath: String?): Storage {
        val basePath = if (basePath == null) {
            rootPath
        } else {
            Paths.get(rootPath.toString(), basePath)
        }

        // make sure basePath is not a parent of rootPath
        basePath.validateNotParent(rootPath)

        return FileStorage(basePath)
    }
}