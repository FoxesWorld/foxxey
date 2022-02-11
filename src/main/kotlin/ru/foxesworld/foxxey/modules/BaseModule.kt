package ru.foxesworld.foxxey.modules

import mu.KotlinLogging
import org.koin.dsl.module
import ru.foxesworld.foxxey.commands.Command
import ru.foxesworld.foxxey.logging.wrappedRunWithoutResult
import ru.foxesworld.foxxey.modules.Module.Info
import ru.foxesworld.foxxey.modules.Module.State
import org.koin.core.module.Module as KoinModule

private val log = KotlinLogging.logger { }

/**
 * @author vie10
 **/
abstract class BaseModule(
    override val info: Info,
    module: (KoinModule.() -> Unit)? = null,
    commands: (ArrayList<Command>.() -> Unit)? = null
) : Module {

    private val module: KoinModule = module {
        module?.run { this() }
    }
    override val commands: List<Command> = arrayListOf<Command>().apply {
        commands?.run { this() }
    }
    override val state: State
        get() = _state
    private var _state: State = State.Unloaded

    final override suspend fun start() = log.wrappedRunWithoutResult(
        target = "module $info",
        verb = "start"
    ) {
        onStart()
        _state = State.Started
    }

    protected abstract fun onStart()

    final override suspend fun stop() = log.wrappedRunWithoutResult(
        target = "module $module",
        verb = "stop"
    ) {
        onStop()
        _state = State.Stopped
    }

    protected abstract fun onStop()

    final override suspend fun load() {
        getKoin().loadModules(listOf(module))
        _state = State.Loaded
    }

    final override suspend fun unload() {
        getKoin().unloadModules(listOf(module))
        _state = State.Unloaded
    }
}
