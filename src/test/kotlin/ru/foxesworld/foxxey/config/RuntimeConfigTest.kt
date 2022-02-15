package ru.foxesworld.foxxey.config

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import ru.foxesworld.foxxey.config.RuntimeConfig.removeFromRuntime
import ru.foxesworld.foxxey.config.RuntimeConfig.runtimeValue
import ru.foxesworld.foxxey.config.RuntimeConfig.uploadToRuntime

/**
 * @author vie10
 **/
class RuntimeConfigTest : BehaviorSpec({

    afterEach {
        RuntimeConfig.clean()
    }

    given("empty runtime config") {
        `when`("assigns value") {
            then("the same value is accessible") {
                val value = "some text"
                TestConfig::notNullName.runtimeValue = value

                TestConfig::notNullName.runtimeValue shouldBe value
            }
        }

        `when`("uploads object") {
            then("the object fields is accessible") {
                val config = TestConfig()
                config.uploadToRuntime()

                TestConfig::notNullName.runtimeValue shouldBe config.notNullName
            }
        }

        `when`("remove config") {
            then("does not throw") {
                shouldNotThrowAny {
                    TestConfig::class.removeFromRuntime()
                }
            }
        }

        `when`("invokes nullable value") {
            then("returns null") {
                TestConfig::nullableName.runtimeValue shouldBe null
            }
        }

        `when`("invokes not null value") {
            then("throws NullPointerException") {
                shouldThrow<NullPointerException> {
                    TestConfig::notNullName.runtimeValue
                }
            }
        }
    }

    given("runtime config with uploaded object") {
        `when`("removes uploaded object") {
            then("invoke not null value throws NullPointerException") {
                TestConfig().uploadToRuntime()
                TestConfig::class.removeFromRuntime()

                shouldThrow<NullPointerException> {
                    TestConfig::notNullName.runtimeValue
                }
            }
        }

        `when`("cleans") {
            then("invoke not null value throws NullPoinerException") {
                TestConfig().uploadToRuntime()
                RuntimeConfig.clean()

                shouldThrow<NullPointerException> {
                    TestConfig::notNullName.runtimeValue
                }
            }
        }
    }
}) {

    data class TestConfig(
        val nullableName: String? = "name",
        val notNullName: String = "name"
    )
}
