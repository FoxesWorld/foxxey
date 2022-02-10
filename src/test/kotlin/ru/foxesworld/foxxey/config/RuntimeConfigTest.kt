package ru.foxesworld.foxxey.config

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import ru.foxesworld.foxxey.config.RuntimeConfig.runtimeValue
import ru.foxesworld.foxxey.config.RuntimeConfig.uploadToRuntime

/**
 * @author vie10
 */
internal class RuntimeConfigTest {

    @BeforeEach
    fun cleanRuntimeConfig() {
        RuntimeConfig.clean()
    }

    @Test
    fun `GIVEN clean runtime config WHEN invokes value THEN throws NullPointerException`() {
        val config = TestConfig()
        config.uploadToRuntime()
        RuntimeConfig.clean()
        assertThrows<NullPointerException> {
            TestConfig::name.runtimeValue
        }
    }

    @Test
    fun `GIVEN clean runtime config WHEN invokes nullable value THEN returns null`() {
        val config = TestConfig()
        config.uploadToRuntime()
        RuntimeConfig.clean()
        assertTrue {
            TestConfig::nullableName.runtimeValue == null
        }
    }

    @Test
    fun `GIVEN empty runtime config WHEN invokes value THEN throws NullPointerException`() {
        assertThrows<NullPointerException> {
            TestConfig::name.runtimeValue
        }
    }

    @Test
    fun `GIVEN empty runtime config WHEN invokes nullable value THEN returns null`() {
        assertTrue {
            TestConfig::nullableName.runtimeValue == null
        }
    }

    @Test
    fun `GIVEN assigned value WHEN invokes THEN returns the same value`() {
        val value = "some another text"
        TestConfig::nullableName.runtimeValue = value
        assertTrue {
            TestConfig::nullableName.runtimeValue == value
        }
    }

    @Test
    fun `GIVEN uploaded object WHEN invokes value THEN returns the same value`() {
        val config = TestConfig()
        config.uploadToRuntime()
        assertTrue {
            TestConfig::nullableName.runtimeValue == config.nullableName
        }
    }

    @Test
    fun `GIVEN uploaded object WHEN invokes nullable value THEN returns the same value`() {
        val config = TestConfig(null)
        config.uploadToRuntime()
        assertTrue {
            TestConfig::nullableName.runtimeValue == config.nullableName
        }
    }

    data class TestConfig(
        val nullableName: String? = "name",
        val name: String = "name"
    )
}
