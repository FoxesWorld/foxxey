package ru.foxesworld.foxxey

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import mu.KotlinLogging
import org.koin.core.component.inject
import org.koin.core.qualifier.named
import ru.foxesworld.foxxey.Foxxey.*
import ru.foxesworld.foxxey.logging.debugExceptionAndInfoMessage
import ru.foxesworld.foxxey.logging.wrappedRunWithoutResult
import ru.foxesworld.foxxey.modules.Module

private val log = KotlinLogging.logger { }

/**
 * @author vie10
 **/
@DelicateCoroutinesApi
abstract class FoxxeyBase : Foxxey {

    final override val state: State
        get() = _state
    private var _state: State = State.Stopped
    override val buildInfo: BuildInfo by inject()
    override val modules: List<Module> = arrayListOf()
    override val config: Config by inject()
    override val scope: CoroutineScope by inject(named(Foxxey.COROUTINE_SCOPE_NAME))

    final override suspend fun start() = wrappedRunWithoutResult(
        logging = {
            onStart = {
                log.info { "Starting Foxxey.." }
            }
            onFailure = {
                log.debugExceptionAndInfoMessage(it) { "Starting Foxxey failed." }
            }
            onSuccess = { measuredMillis, _ ->
                log.info { "Foxxey started in $measuredMillis millis." }
            }
        }
    ) {
        if (state == State.Started) {
            throw IllegalStateException("Foxxey already started")
        }
        onStart()
        _state = State.Started
    }

    protected abstract suspend fun onStart()

    final override suspend fun stop() = wrappedRunWithoutResult(
        logging = {
            onStart = {
                log.info { "Stopping Foxxey.." }
            }
            onFailure = {
                log.debugExceptionAndInfoMessage(it) { "Stopping Foxxey failed." }
            }
            onSuccess = { measuredMillis, _ ->
                log.info { "Foxxey stopped in $measuredMillis millis." }
            }
        }
    ) {
        if (state == State.Stopped) {
            throw IllegalStateException("Foxxey already stopped")
        }
        onStop()
        _state = State.Stopped
    }

    protected abstract suspend fun onStop()

    final override suspend fun restart() = wrappedRunWithoutResult(
        logging = {
            onStart = {
                log.info { "Restarting Foxxey.." }
            }
            onFailure = {
                log.debugExceptionAndInfoMessage(it) { "Restarting Foxxey failed." }
            }
            onSuccess = { measuredMillis, _ ->
                log.info { "Foxxey restarted in $measuredMillis millis." }
            }
        }
    ) {
        if (state == State.Started) {
            stop()
        }
        start()
    }

    final override suspend fun addModule(module: Module) {
        (modules as MutableList).add(module)
    }

    final override suspend fun remModule(module: Module) {
        (modules as MutableList).remove(module)
    }

    override suspend fun remAllModules() {
        (modules as MutableList).clear()
    }

    final override fun toString(): String {
        return "foxxey v. ${buildInfo.version}"
    }
}
