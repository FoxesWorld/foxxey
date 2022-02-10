package ru.foxesworld.foxxey.config.hoplite

import com.sksamuel.hoplite.ConfigException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.exceptions.base.MockitoException
import ru.foxesworld.foxxey.config.ConfigFile
import ru.foxesworld.foxxey.config.ConfigFileProvider
import ru.foxesworld.foxxey.config.ConfigInfo
import kotlin.reflect.jvm.jvmName
import kotlin.test.assertTrue

private const val DEFAULT_NAME = "default name"

/**
 * @author vie10
 */
internal class HopliteConfigLoaderTest {

    @Test
    fun `GIVEN config info with not existent class name WHEN loads THEN does not throw`() {
        val configInfo = newConfigInfo()
        val content = "{}".toByteArray()
        val configLoader = new {
            provideFile(configInfo, content)
        }
        assertDoesNotThrow {
            configLoader.load(configInfo)
        }
    }

    @Test
    fun `GIVEN config file provider throwing exception on provide WHEN loads THEN throws the same exception`() {
        val exception = MockitoException("")
        val configInfo = newConfigInfo(className = ConfigWithoutRequiredFields::class.jvmName)
        val configLoader = new {
            `when`(provide(configInfo)).thenThrow(exception)
        }
        assertTrue {
            configLoader.load(configInfo).exceptionOrNull() == exception
        }
    }

    @Test
    fun `GIVEN config file throwing exception on read WHEN loads THEN throws the same exception`() {
        val exception = MockitoException("")
        val configInfo = newConfigInfo(className = ConfigWithRequiredFields::class.jvmName)
        val configLoader = new {
            provideFile(configInfo) {
                `when`(read()).thenThrow(exception)
            }
        }
        assertTrue {
            configLoader.load(configInfo).exceptionOrNull() == exception
        }
    }

    @Test
    fun `GIVEN config file contains required name WHEN loads THEN does not throw`() {
        val configInfo = newConfigInfo(className = ConfigWithRequiredFields::class.jvmName)
        val content = "{\"name\":\"$DEFAULT_NAME\"}".toByteArray()
        val configLoader = new {
            provideFile(configInfo, content)
        }
        assertDoesNotThrow {
            configLoader.load(configInfo).getOrThrow()
        }
    }

    @Test
    fun `GIVEN config file contains not required name WHEN loads THEN throws ConfigException`() {
        val configInfo = newConfigInfo(className = ConfigWithRequiredFields::class.jvmName)
        val content = "{}".toByteArray()
        val configLoader = new {
            provideFile(configInfo, content)
        }
        assertThrows<ConfigException> {
            configLoader.load(configInfo).getOrThrow()
        }
    }

    @Test
    fun `GIVEN config file contains name WHEN loads THEN returns model with the same name`() {
        val configInfo = newConfigInfo(className = ConfigWithoutRequiredFields::class.jvmName)
        val name = "another name"
        val content = "{\"name\":\"$name\"}".toByteArray()
        val configLoader = new {
            provideFile(configInfo, content)
        }
        val configInstance = configLoader.load(configInfo).getOrThrow() as ConfigWithoutRequiredFields
        assertTrue {
            configInstance.name == name
        }
    }

    @Test
    fun `GIVEN config file contains not name WHEN loads THEN returns model with default name`() {
        val configInfo = newConfigInfo(className = ConfigWithoutRequiredFields::class.jvmName)
        val content = "{}".toByteArray()
        val configLoader = new {
            provideFile(configInfo, content)
        }
        val configInstance = configLoader.load(configInfo).getOrThrow() as ConfigWithoutRequiredFields
        assertTrue {
            configInstance.name == DEFAULT_NAME
        }
    }

    private fun new(
        extension: String = "json",
        block: (ConfigFileProvider.() -> Unit)? = null
    ): HopliteConfigLoader {
        val configFileProvider = mock(ConfigFileProvider::class.java)
        block?.run { configFileProvider.block() }
        return HopliteConfigLoader(configFileProvider, extension)
    }

    private fun ConfigFileProvider.provideFile(configInfo: ConfigInfo, content: ByteArray) {
        provideFile(configInfo) {
            `when`(read()).then { content }
        }
    }

    private fun ConfigFileProvider.provideFile(configInfo: ConfigInfo, block: ConfigFile.() -> Unit) {
        val file = mock(ConfigFile::class.java).apply(block)
        `when`(provide(configInfo)).then { file }
    }

    private fun newConfigInfo(name: String = "", group: String = "", className: String = "") =
        ConfigInfo(name, group, className)

    data class ConfigWithoutRequiredFields(
        val name: String = DEFAULT_NAME
    )

    data class ConfigWithRequiredFields(
        val name: String
    )
}
