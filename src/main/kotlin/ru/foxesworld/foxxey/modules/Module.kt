package ru.foxesworld.foxxey.modules

import org.koin.core.component.KoinComponent
import ru.foxesworld.foxxey.commands.Command
import kotlin.reflect.KClass

/**
 * @author vie10
 **/
interface Module : KoinComponent {

    val state: State
    val info: Info

    suspend fun start()

    suspend fun stop()

    suspend fun load()

    suspend fun unload()

    interface Info {

        val id: String
        val name: String
        val version: Version
        val dependencies: Set<Dependency>
        val description: String
        val config: Set<KClass<out Any>>
        val commands: Set<KClass<Command>>

        data class Version(val name: String, val code: Int)

        data class Dependency(val id: String, val versionCode: VersionCode) {

            data class VersionCode(val from: Int, val to: Int)
        }
    }

    enum class State {
        Unloaded,
        Loaded,
        Stopped,
        Started
    }
}
