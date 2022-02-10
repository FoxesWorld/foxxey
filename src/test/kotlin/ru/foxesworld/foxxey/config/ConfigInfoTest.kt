package ru.foxesworld.foxxey.config

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import kotlin.reflect.jvm.jvmName

/**
 * @author vie10
 */
internal class ConfigInfoTest {

    @Test
    fun `GIVEN config info with existent class name WHEN invokes clazz THEN does not throw`() {
        val configInfo = new(className = ConfigInfoTest::class.jvmName)
        assertDoesNotThrow {
            configInfo.clazz
        }
    }

    @Test
    fun `GIVEN config info with not existent class name WHEN invokes clazz THEN throws ClassNotFoundException`() {
        val configInfo = new()
        assertThrows<ClassNotFoundException> {
            configInfo.clazz
        }
    }

    private fun new(name: String = "", group: String = "", className: String = "") = ConfigInfo(name, group, className)
}
