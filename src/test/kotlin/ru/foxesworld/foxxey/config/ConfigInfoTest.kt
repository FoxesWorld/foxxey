package ru.foxesworld.foxxey.config

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import kotlin.reflect.jvm.jvmName

/**
 * @author vie10
 **/
class ConfigInfoTest : BehaviorSpec({

    given("config info with existent class jvm name") {
        `when`("invokes class") {
            then("returns class with the jvm name") {
                val classJvmName = ConfigInfoTest::class.jvmName
                val configInfo = ConfigInfo("", "", classJvmName)

                configInfo.clazz.jvmName shouldBe classJvmName
            }
        }
    }

    given("config info with not existent class jvm name") {
        `when`("invokes class") {
            then("throws ClassNotFoundException") {
                val configInfo = ConfigInfo("", "", "")

                shouldThrow<ClassNotFoundException> {
                    configInfo.clazz
                }
            }
        }
    }
})
