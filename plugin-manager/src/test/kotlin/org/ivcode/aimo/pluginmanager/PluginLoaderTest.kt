package org.ivcode.aimo.pluginmanager

import org.ivcode.aimo.pluginmanager.loader.PluginClassloaderManager
import org.ivcode.aimo.pluginmanager.loader.PluginLoaderService
import org.ivcode.aimo.pluginmanager.loader.PluginType
import org.ivcode.aimo.utils.FileStorge
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.nio.file.Path
import kotlin.jvm.JvmStatic

private const val TEST_PLUGIN_PATH = "./test-plugin-storage"

class PluginLoaderTest {

    companion object {

        val testPluginDirectory: Path = Path.of(TEST_PLUGIN_PATH)

        @JvmStatic
        @BeforeAll
        fun beforeAll() {
            // Setup code before all tests
            testPluginDirectory.toFile().apply {
                if (!exists()) {
                    mkdirs()
                }
            }
        }

        @JvmStatic
        @AfterAll
        fun afterAll() {
            // Cleanup code after all tests
            testPluginDirectory.toFile().apply {
                if (exists()) {
                    deleteRecursively()
                }
            }
        }
    }

    val pluginStorage = FileStorge()
    var pluginClassloaderManager: PluginClassloaderManager? = null

    var loader: PluginLoaderService? = null;

    @BeforeEach
    fun beforeEach() {
        pluginStorage.basePath = null

        pluginClassloaderManager = PluginClassloaderManager(
            directory = testPluginDirectory
        )

        loader = PluginLoaderService(
            pluginStorage = pluginStorage,
            pluginClassloaderManager!!
        )
    }

    @AfterEach
    fun afterEach() {
        pluginClassloaderManager?.close()
        pluginClassloaderManager = null
        loader = null
    }


    @Test
    fun simpleTest() {
        pluginStorage.basePath = "../test/test-plugin/build/"

        val entryPoint = loader!!.getPlugin(PluginType.TOOL, "test-plugin")
        assert(entryPoint != null)

        val plugin = entryPoint!!.configure()
        plugin.systemMessages.forEach {
            println("System Message: ${it.getMessage()}")
        }
    }
}