package org.ivcode.aimo.service.pluginmanager

import org.ivcode.aimo.BASE_PATH
import org.ivcode.aimo.TestConfig
import org.ivcode.aimo.utils.deleteRecursivelyWithRetry
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.io.File
import java.nio.file.Path

@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [PluginManagerConfig::class, TestConfig::class])
@Execution(ExecutionMode.SAME_THREAD)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class PluginManagerServiceTest {

    companion object {

        @BeforeAll
        @JvmStatic
        fun beforeAll() {
            Path.of(BASE_PATH).deleteRecursivelyWithRetry()
        }
    }

    @AfterEach
    fun afterEach() {
        Path.of(BASE_PATH).deleteRecursivelyWithRetry()
    }

    @Autowired
    internal lateinit var service: PluginManagerService

    @Test
    fun `test enabling and disabling a plugin`() {
        // Make sure the dependent plugin exists
        val pluginFile = File("../test/test-plugin/build/plugins/tool/test-plugin.jar")
        assert(pluginFile.exists()) {"Plugin file does not exist: ${pluginFile.absolutePath}"}

        // Upload the plugin into the manager
        val pluginInfo = service.uploadPlugin(pluginFile.inputStream())

        // Enable the plugin and verify we have an entry point
        service.enablePlugin(pluginInfo.metadata.id).run {
            assertNotNull(this)
        }

        // Verify the plugin info shows it as enabled
        service.getPluginInfo(pluginInfo.metadata.id).run {
            assertNotNull(this)
            assertEquals(true, this!!.enabled)
        }

        // Verify the active plugins contains it
        service.getActivePlugins().run {
            assertEquals(1, this.size)
        }

        // Disable the plugin
        service.disablePlugin(pluginInfo.metadata.id)

        // Verify the plugin info shows it as disabled
        service.getPluginInfo(pluginInfo.metadata.id).run {
            assertNotNull(this)
            assertEquals(false, this!!.enabled)
        }

        // Verify the active plugins does not contain it
        service.getActivePlugins().run {
            assertEquals(0, this.size)
        }
    }
}

