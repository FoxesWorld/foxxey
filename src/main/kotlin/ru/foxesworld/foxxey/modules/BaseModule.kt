package ru.foxesworld.foxxey.modules

import mu.KotlinLogging
import org.koin.dsl.module
import ru.foxesworld.foxxey.commands.Command
import ru.foxesworld.foxxey.config.ConfigInfo
import ru.foxesworld.foxxey.logging.wrappedRunWithoutResult
import ru.foxesworld.foxxey.modules.Module.State
import kotlin.reflect.full.companionObjectInstance
import org.koin.core.module.Module as KoinModule

private val log = KotlinLogging.logger { }

/**
 * @author vie10
 **/
abstract class BaseModule(
    providing: (KoinModule.() -> Unit)? = null
) : Module {

    private val koinModule: KoinModule by lazy { module { providing?.invoke(this) } }
    override val commands: Set<Command> = hashSetOf()
    override val configInfo: Set<ConfigInfo> = hashSetOf()
    final override val info: Info by lazy { this::class.companionObjectInstance as Info }
    final override val state: State
        get() = _state
    private var _state: State = State.Unloaded

    final override suspend fun start() = log.wrappedRunWithoutResult(
        target = "module $this", verb = "start"
    ) {
        if (state == State.Started) {
            throw IllegalStateException("Module already started")
        }
        if (state == State.Unloaded) {
            throw IllegalStateException("Module should be loaded before start")
        }
        onStart()
        _state = State.Started
    }

    protected abstract fun onStart()

    final override suspend fun stop() = log.wrappedRunWithoutResult(
        target = "module $this", verb = "stop"
    ) {
        if (state != State.Started) {
            throw IllegalStateException("Module already stopped")
        }
        onStop()
        _state = State.Stopped
    }

    protected abstract fun onStop()

    final override suspend fun load() = log.wrappedRunWithoutResult(
        target = "module $this", verb = "load"
    ) {
        if (state != State.Unloaded) {
            throw IllegalStateException("Module already loaded")
        }
        getKoin().loadModules(listOf(koinModule))
        onLoad()
        _state = State.Loaded
    }

    protected open fun onLoad() {}

    final override suspend fun unload() = log.wrappedRunWithoutResult(
        target = "module $this", verb = "unload"
    ) {
        if (state == State.Started) {
            throw IllegalStateException("Module should be stopped before unload")
        }
        if (state == State.Unloaded) {
            throw IllegalStateException("Module already unloaded")
        }
        getKoin().unloadModules(listOf(koinModule))
        onUnload()
        _state = State.Loaded
    }

    protected open fun onUnload() {}

    final override suspend fun addCommand(command: Command) {
        (commands as MutableSet).add(command)
    }

    final override suspend fun addConfigInfo(configInfo: ConfigInfo) {
        (this.configInfo as MutableSet).add(configInfo)
    }

    final override fun toString(): String {
        return "${info.name} v. ${info.version}"
    }

    final override fun hashCode(): Int {
        var result = koinModule.hashCode()
        result = 31 * result + info.hashCode()
        result = 31 * result + state.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Module) return false

        if (info.id != other.info.id) return false

        return true
    }

    open class Info(
        override val id: String,
        override val version: Module.Info.Version,
        override val description: String = "The module hasn't description for now",
        override val name: String = id,
        override val dependencies: Set<Module.Info.Dependency> = emptySet(),
    ) : Module.Info {


        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Info

            if (id != other.id) return false
            if (version != other.version) return false
            if (description != other.description) return false
            if (name != other.name) return false
            if (dependencies != other.dependencies) return false

            return true
        }

        override fun hashCode(): Int {
            var result = id.hashCode()
            result = 31 * result + version.hashCode()
            result = 31 * result + description.hashCode()
            result = 31 * result + name.hashCode()
            result = 31 * result + dependencies.hashCode()
            return result
        }
    }
}
