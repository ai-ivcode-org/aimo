package org.ivcode.aimo.service.pluginmanager.scanner

import org.ivcode.aimo.BASE_PATH
import org.ivcode.aimo.STORAGE_ROOT_PATH
import org.ivcode.aimo.TestConfig
import org.ivcode.aimo.service.pluginmanager.LOCK_SESSION
import org.ivcode.aimo.service.pluginmanager.PluginManagerConfig
import org.ivcode.aimo.service.pluginmanager.info.PluginInfoService
import org.ivcode.common.data.session.Session
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.nio.file.Files
import java.nio.file.Path

@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [PluginManagerConfig::class, TestConfig::class])
@Execution(ExecutionMode.SAME_THREAD)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class StartupScannerTest {

    companion object {
        @JvmStatic
        @BeforeAll
        fun beforeAll() {
            Path.of(BASE_PATH).toFile().deleteRecursively()
        }
    }

    @AfterEach
    fun afterEach() {
        Path.of(BASE_PATH).toFile().deleteRecursively()
    }

    @Autowired
    internal lateinit var infoService: PluginInfoService

    @Autowired
    @Qualifier(LOCK_SESSION)
    internal lateinit var lockSession: Session<String>

    @Autowired
    internal lateinit var pluginScanner: PluginScanner

    @Test
    fun `test adding a new package`() {
        val pluginId = "test-plugin"

        // Manually copy the plugin file to the storage
        run {
            // plugin file to copy
            val pluginFile = Path.of("../test/test-plugin/build/plugins/tool/test-plugin.jar")
            assert(Files.exists(pluginFile)) {"Plugin file does not exist: $pluginFile"}

            // the target file location. This is in the plugin repository storage
            val targetFile = Path.of(STORAGE_ROOT_PATH, "test-plugin.jar")
            Files.createDirectories(targetFile.parent)

            // Copy the plugin into storage
            Files.copy(pluginFile, targetFile)
        }

        // Make sure the plugin is unknown
        assertNull(infoService.getPlugin(pluginId))

        // Scan for plugins
        pluginScanner.scanPlugins()

        // Make sure plugin is now known
        assertNotNull(infoService.getPlugin(pluginId))
    }

    @Test
    fun `test skipping if file is locked`() {
        val fileName = "test-plugin.jar"
        val pluginId = "test-plugin"

        // Manually copy the plugin file to the storage
        run {
            // plugin file to copy
            val pluginFile = Path.of("../test/test-plugin/build/plugins/tool/$fileName")
            assert(Files.exists(pluginFile)) {"Plugin file does not exist: $pluginFile"}

            // the target file location. This is in the plugin repository storage
            val targetFile = Path.of(STORAGE_ROOT_PATH, fileName)
            Files.createDirectories(targetFile.parent)

            // Copy the plugin into storage
            Files.copy(pluginFile, targetFile)
        }

        // Create a lock on the file
        lockSession.putIfAbsent(fileName, fileName)

        // Make sure the plugin is unknown
        assertNull(infoService.getPlugin(pluginId))

        // Scan for plugins
        pluginScanner.scanPlugins()

        // Make sure the plugin is still unknown since the file was locked
        assertNull(infoService.getPlugin(pluginId))
    }
}
