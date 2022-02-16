package ru.foxesworld.foxxey.modules

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.result.shouldBeFailure
import io.kotest.matchers.result.shouldBeFailureOfType
import io.kotest.matchers.result.shouldBeSuccess
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.every
import io.mockk.mockk
import ru.foxesworld.foxxey.commands.BaseCommand
import kotlin.reflect.jvm.jvmName

class JarModuleSourceTest : BehaviorSpec({
    given("jar without classes") {
        val classLoader = mockk<JarClassLoader>()
        every { classLoader.addJar(any()) } returns Unit

        val jar = mockk<JarFile>()
        every { classLoader.findClassInJars(any()) } throws NullPointerException()
        every { jar.classJVMNames } returns emptyList()

        val source = JarModuleSource(classLoader, jar)

        `when`("imports module") {
            then("returns failure") {
                source.importModule().shouldBeFailure()
            }

            then("throws ModuleNotFoundException") {
                source.importModule().shouldBeFailureOfType<ModuleNotFoundException>()
            }
        }
    }

    given("jar with module class") {
        val moduleClassName = TestModule::class.jvmName

        val classLoader = mockk<JarClassLoader>()
        every { classLoader.addJar(any()) } returns Unit
        val jar = mockk<JarFile>()
        every { classLoader.findClassInJars(moduleClassName) } returns TestModule::class
        every { jar.classJVMNames } returns listOf(moduleClassName)

        val source = JarModuleSource(classLoader, jar)

        `when`("imports module") {
            then("returns success") {
                source.importModule().shouldBeSuccess()
            }

            then("returns the class instance") {
                source.importModule().shouldBeSuccess {
                    it.shouldBeInstanceOf<TestModule>()
                }
            }

            then("module info references its companion object") {
                source.importModule().shouldBeSuccess {
                    it.info.shouldBeInstanceOf<TestModule.Companion>()
                }
            }
        }

        and("command class") {
            val commandClassName = TestCommand::class.jvmName

            every { classLoader.findClassInJars(commandClassName) } returns TestCommand::class
            every { jar.classJVMNames } returns listOf(moduleClassName, commandClassName)

            `when`("imports module") {
                then("returns module with the command") {
                    source.importModule().shouldBeSuccess {
                        it.commands.size shouldBe 1
                        it.commands.first().shouldBeInstanceOf<TestCommand>()
                    }
                }
            }
        }

        and("config class") {
            val configClassName = TestConfig::class.jvmName

            every { classLoader.findClassInJars(configClassName) } returns TestConfig::class
            every { jar.classJVMNames } returns listOf(moduleClassName, configClassName)

            `when`("imports module") {
                then("returns module with the config info") {
                    source.importModule().shouldBeSuccess {
                        it.configInfo.size shouldBe 1
                        it.configInfo.first().clazz shouldBe TestConfig::class
                    }
                }
            }
        }
    }
}) {

    @Config("test", "")
    data class TestConfig(val someValue: String)

    class TestCommand : BaseCommand("", "", "", "")

    class TestModule : BaseModule() {

        override fun onStart() {}

        override fun onStop() {}

        companion object : Info("", Module.Info.Version("", 1))
    }
}
