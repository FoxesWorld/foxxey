package ru.foxesworld.foxxey.config.hoplite

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.result.shouldBeFailure
import io.kotest.matchers.result.shouldBeSuccess
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import ru.foxesworld.foxxey.config.ConfigFile
import ru.foxesworld.foxxey.config.ConfigFileProvider
import ru.foxesworld.foxxey.config.ConfigInfo
import ru.foxesworld.foxxey.config.FolderConfigFileProvider
import java.io.File
import kotlin.reflect.jvm.jvmName

private const val EXTENSION = "json"

/**
 * @author vie10
 **/
class HopliteConfigLoaderTest : BehaviorSpec({

    given("not existent config info") {
        `when`("loads") {
            then("failure") {
                val notExistentFolder = File("some not exists folder")
                val fileProvider = FolderConfigFileProvider(notExistentFolder)
                val instance = HopliteConfigLoader(fileProvider, EXTENSION)
                val notExistentConfigInfo = ConfigInfo("some not existent config info", "", "")

                instance.load(notExistentConfigInfo).shouldBeFailure()
            }
        }
    }

    given("existent config info") {
        val existentConfigInfo = ConfigInfo("name", "", TestConfig::class.jvmName)

        and("config file without optional field") {
            `when`("loads") {
                val configFile = mockk<ConfigFile>()
                every { configFile.read() } returns Result.success("{}".toByteArray())
                val fileProvider = mockk<ConfigFileProvider>()
                every { fileProvider.provide(existentConfigInfo) } returns Result.success(configFile)
                val instance = HopliteConfigLoader(fileProvider, EXTENSION)

                then("success") {
                    instance.load(existentConfigInfo).shouldBeSuccess()
                }

                then("assigns default value") {
                    instance.load(existentConfigInfo).shouldBeSuccess {
                        (it as TestConfig).name shouldBe TestConfig.DEFAULT_NAME
                    }
                }
            }
        }

        `when`("loads") {
            then("success") {
                val configFile = mockk<ConfigFile>()
                every { configFile.read() } returns Result.success("{\"name\":\"name\"}".toByteArray())
                val fileProvider = mockk<ConfigFileProvider>()
                every { fileProvider.provide(existentConfigInfo) } returns Result.success(configFile)
                val instance = HopliteConfigLoader(fileProvider, EXTENSION)

                instance.load(existentConfigInfo).shouldBeSuccess()
            }
        }
    }
}) {

    data class TestConfig(val name: String = DEFAULT_NAME) {
        companion object {
            const val DEFAULT_NAME = "default name"
        }
    }
}
