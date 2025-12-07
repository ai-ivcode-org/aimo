package org.ivcode.beeboop.datastore.storage.impl.filestorage

import org.ivcode.beeboop.datastore.storage.PROPERTY_NAME_STORAGE_TYPE
import org.ivcode.beeboop.datastore.storage.Storage
import org.ivcode.beeboop.datastore.storage.StorageFactory
import org.ivcode.beeboop.utils.validateNotParent
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths

@Component
@ConditionalOnProperty(name = [PROPERTY_NAME_STORAGE_TYPE], havingValue = "file", matchIfMissing = true)
class FileStorageFactory(
    private val rootPath: Path = Path.of("./data/storage")
): StorageFactory {

    override fun createStorage(basePath: String): Storage {
        val basePath = Paths.get(rootPath.toString(), basePath)

        // make sure basePath is not a parent of rootPath
        basePath.validateNotParent(rootPath)

        return FileStorage(basePath)
    }
}