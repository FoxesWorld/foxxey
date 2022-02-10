package ru.foxesworld.foxxey.config

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import java.io.File
import org.junit.jupiter.api.Assertions.*

/**
 * @author vie10
 */
internal class FolderConfigFileProviderTest {

    private val nonExistentFolder = File("some folder that not exists")
    private val emptyConfigInfo = newConfigInfo()

    @Test
    fun `GIVEN config info with group WHEN provides config file THEN config file path contains name and group`() {
        val configInfo = ConfigInfo("name", "group", "")
        val instance = FolderConfigFileProvider(nonExistentFolder)
        val configFile = instance.provide(configInfo).getOrThrow()
        assertTrue {
            configFile.path.run {
                contains(configInfo.name) && contains(configInfo.group)
            }
        }
    }

    @Test
    fun `GIVEN config info without group WHEN provides config file THEN config file path contains name`() {
        val configInfo = ConfigInfo("name", "", "")
        val instance = FolderConfigFileProvider(nonExistentFolder)
        val configFile = instance.provide(configInfo).getOrThrow()
        assertTrue {
            configFile.path.contains(configInfo.name)
        }
    }

    @Test
    fun `GIVEN folder WHEN provides config file THEN config file path starts with the folder`() {
        val configInfo = newConfigInfo(name = "name")
        val instance = FolderConfigFileProvider(nonExistentFolder)
        val configFile = instance.provide(configInfo).getOrThrow()
        assertTrue {
            configFile.path.startsWith(nonExistentFolder.name)
        }
    }

    @Test
    fun `GIVEN jar resources folder WHEN provides config file THEN does not throw`() {
        val resourceFilePath = FolderConfigFileProvider::class.java.getResource("/test")!!.file
        val testFolder = File(resourceFilePath)
        val instance = FolderConfigFileProvider(testFolder)
        assertDoesNotThrow {
            instance.provide(emptyConfigInfo).getOrThrow()
        }
    }

    @Test
    fun `GIVEN non-existent folder WHEN provides config file THEN does not throw`() {
        val instance = FolderConfigFileProvider(nonExistentFolder)
        assertDoesNotThrow {
            instance.provide(emptyConfigInfo).getOrThrow()
        }
    }

    private fun newConfigInfo(name: String = "", group: String = "", className: String = "") =
        ConfigInfo(name, group, className)
}
