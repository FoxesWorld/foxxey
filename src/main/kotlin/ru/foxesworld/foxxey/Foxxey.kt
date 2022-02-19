package ru.foxesworld.foxxey

import com.sksamuel.hoplite.ConfigAlias
import kotlinx.coroutines.CoroutineScope
import org.koin.core.component.KoinComponent
import ru.foxesworld.foxxey.config.ConfigInfo
import ru.foxesworld.foxxey.modules.Module
import kotlin.reflect.jvm.jvmName

/**
 * @author vie10
 **/
interface Foxxey : KoinComponent {

    val buildInfo: BuildInfo
    val state: State
    val modules: List<Module>
    val scope: CoroutineScope
    val config: Config

    suspend fun start()

    suspend fun stop()

    suspend fun restart()

    suspend fun addModule(module: Module)

    suspend fun remModule(module: Module)

    suspend fun remAllModules()

    enum class State {
        Stopped,
        Started
    }

    data class BuildInfo(
        @ConfigAlias("v")
        val version: String
    ) {

        companion object {
            val info = ConfigInfo("build-info", "", BuildInfo::class.jvmName)
        }
    }

    data class Config(
        @ConfigAlias("threads-count")
        val threadsCount: Int,
        @ConfigAlias("modules-folder")
        val modulesFolder: String
    ) {

        companion object {
            val info = ConfigInfo("foxxey", Config::class.jvmName)
        }
    }

    companion object {

        const val COROUTINE_SCOPE_NAME = "foxxey"
    }
}
