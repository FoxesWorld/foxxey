package ru.foxesworld.foxxey.modules

import kotlinx.serialization.SerialName
import org.koin.core.component.KoinComponent
import ru.foxesworld.foxxey.commands.Command
import ru.foxesworld.foxxey.config.ConfigInfo

/**
 * @author vie10
 **/
interface Module : KoinComponent {

    val info: Info
    val state: State
    val commands: List<Command>

    suspend fun start()

    suspend fun stop()

    suspend fun load()

    suspend fun unload()

    data class Info(
        @SerialName("id")
        val id: String,
        @SerialName("name")
        val name: String,
        @SerialName("version-code")
        val versionCode: String,
        @SerialName("version")
        val version: String,
        @SerialName("dependencies")
        val dependencies: Set<Dependency>,
        @SerialName("config")
        val config: Set<ConfigInfo>
    ) {

        data class Dependency(
            @SerialName("id")
            val id: String,
            @SerialName("version-code")
            val versionCode: VersionCode
        ) {

            data class VersionCode(
                @SerialName("from")
                val from: Int,
                @SerialName("to")
                val to: Int
            )
        }
    }

    enum class State {
        Unloaded,
        Loaded,
        Stopped,
        Started
    }
}
