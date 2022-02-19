package ru.foxesworld.foxxey

import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.newFixedThreadPoolContext
import kotlinx.coroutines.runBlocking
import org.koin.core.context.startKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.test.KoinTest
import ru.foxesworld.foxxey.modules.Module

@DelicateCoroutinesApi
suspend fun newInstanceWithModule(module: Module, block: suspend FoxxeyBaseImpl.() -> Unit) = FoxxeyBaseImpl().apply {
    runBlocking { addModule(module) }
    block()
}

@DelicateCoroutinesApi
class AsyncFoxxeyTest : BehaviorSpec({

    given("a module") {
        startKoin {
            modules(module {
                single { Foxxey.Config(threadsCount = 8, "") }
                single(named(Foxxey.COROUTINE_SCOPE_NAME)) {
                    CoroutineScope(newFixedThreadPoolContext(8, "Foxxey test"))
                }
            })
        }


        `when`("starts") {
            then("the module loads and starts") {
                val module = mockk<Module>()
                newInstanceWithModule(module) { start() }

                verify { runBlocking { module.load() } }
                verify { runBlocking { module.start() } }
            }

            then("the module starts in an another thread") {
                val module = mockk<Module>()
                every { runBlocking { module.start() } } answers {

                }
                newInstanceWithModule(module) { start() }
            }
        }
        `when`("stops") {
            then("the module stops and unloads") {
                val module = mockk<Module>()
                newInstanceWithModule(module) { start(); stop() }

                verify { runBlocking { module.stop() } }
                verify { runBlocking { module.unload() } }
            }
        }
    }
}), KoinTest
