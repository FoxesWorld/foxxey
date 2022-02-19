package ru.foxesworld.foxxey.config

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.result.shouldBeFailureOfType
import io.kotest.matchers.result.shouldBeSuccess
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContainIgnoringCase
import io.kotest.matchers.string.shouldStartWith
import java.io.File

/**
 * @author vie10
 **/
class FolderConfigFileProviderTest : BehaviorSpec({
    val nonExistentFolder = File("some folder that not exists")
    val someConfigInfo = ConfigInfo("name", "", "")

    given("config info with empty name") {
        `when`("provides config file") {
            then("throws IllegalArgumentException") {
                val configInfoWithEmptyName = ConfigInfo("", "", "")
                val instance = FolderConfigFileProvider(nonExistentFolder)
                instance.provide(configInfoWithEmptyName).shouldBeFailureOfType<IllegalStateException>()
            }
        }
    }

    given("non-existent folder") {
        `when`("provides config file") {
            then("does not throw") {
                val instance = FolderConfigFileProvider(nonExistentFolder)
                instance.provide(someConfigInfo)
            }
        }
    }

    given("existent jar resources file") {
        `when`("provides the file") {
            then("the file exists") {
                val existentJarResourcesFolder =
                    File(FolderConfigFileProviderTest::class.java.getResource("/test")!!.file)
                val existentConfigName = "testfile"
                val existentConfigInfo = ConfigInfo(existentConfigName, "")
                val instance = FolderConfigFileProvider(existentJarResourcesFolder)
                instance.provide(existentConfigInfo).shouldBeSuccess() {
                    it.exists() shouldBe true
                }
            }
        }
    }

    given("jar resources folder") {
        `when`("provides config file") {
            then("success") {
                val instance = FolderConfigFileProvider(nonExistentFolder)
                instance.provide(someConfigInfo).shouldBeSuccess()
            }
        }
    }

    given("folder") {
        `when`("provides config file") {
            then("config file path starts with the folder") {
                val instance = FolderConfigFileProvider(nonExistentFolder)
                val configFile = instance.provide(someConfigInfo)
                configFile.shouldBeSuccess {
                    it.path shouldStartWith nonExistentFolder.name
                }
            }
        }
    }

    given("config info with name") {

        and("empty group") {
            `when`("provides config file") {
                then("config file path contains name") {
                    val name = "name"
                    val configInfo = ConfigInfo(name, "", "")
                    val instance = FolderConfigFileProvider(nonExistentFolder)

                    instance.provide(configInfo).shouldBeSuccess {
                        it.path shouldContainIgnoringCase name
                    }
                }
            }
        }

        and("group") {
            `when`("provides config file") {
                then("config file path contains name and group") {
                    val name = "name"
                    val group = "group"
                    val configInfo = ConfigInfo(name = name, group = group, className = "")
                    val instance = FolderConfigFileProvider(nonExistentFolder)

                    instance.provide(configInfo).shouldBeSuccess {
                        it.path shouldContainIgnoringCase name
                        it.path shouldContainIgnoringCase group
                    }
                }
            }
        }
    }
})
