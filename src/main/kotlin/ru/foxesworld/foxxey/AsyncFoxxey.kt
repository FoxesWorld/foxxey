package ru.foxesworld.foxxey

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.launch
import mu.KotlinLogging
import org.koin.core.component.inject
import ru.foxesworld.foxxey.logging.debugExceptionAndInfoMessage
import ru.foxesworld.foxxey.logging.wrappedRunWithoutResult
import ru.foxesworld.foxxey.modules.*
import java.io.File

private val log = KotlinLogging.logger { }

@DelicateCoroutinesApi
class FoxxeyBaseImpl : FoxxeyBase() {

    private val jarClassLoader: JarClassLoader by inject()
    private val modulesFolderFile: File by lazy { File(config.modulesFolder) }

    override suspend fun onStart() {
        loadModules()
        startModules()
    }

    private suspend fun startModules() = wrappedRunWithoutResult(
        logging = {
            onStart = {
                log.info { "Starting modules.." }
            }
            onFailure = {
                log.debugExceptionAndInfoMessage(it) { "Starting modules failed." }
            }
            onSuccess = { measuredMillis, _ ->
                log.info { "Modules started in $measuredMillis millis." }
            }
        }
    ) {
        modules.forEach { startModule(it) }
    }

    private suspend fun startModule(module: Module) = wrappedRunWithoutResult {
        scope.launch { module.start() }
    }

    private suspend fun stopModules() = wrappedRunWithoutResult(
        logging = {
            onStart = {
                log.info { "Stopping modules.." }
            }
            onFailure = {
                log.debugExceptionAndInfoMessage(it) { "Stopping modules failed." }
            }
            onSuccess = { measuredMillis, _ ->
                log.info { "Modules stopped in $measuredMillis millis." }
            }
        }
    ) {
        modules.forEach { stopModule(it) }
    }

    private suspend fun stopModule(module: Module) = wrappedRunWithoutResult {
        module.stop()
    }

    private suspend fun loadModules() = wrappedRunWithoutResult(
        logging = {
            onStart = {
                log.info { "Loading modules.." }
            }
            onFailure = {
                log.debugExceptionAndInfoMessage(it) { "Loading modules failed." }
            }
            onSuccess = { measuredMillis, _ ->
                log.info { "Modules loaded in $measuredMillis millis." }
            }
        }
    ) {
        modulesFolderFile.listFiles { file: File -> file.extension == "jar" }?.map { JarFileImpl(it) }?.map {
            JarModuleSource(jarClassLoader, it)
        }?.forEach { source ->
            source.importModule().onSuccess {
                addModule(it)
            }
        }
        modules.forEach { loadModule(it) }
    }

    private suspend fun loadModule(module: Module) = wrappedRunWithoutResult {
        module.load()
    }

    private suspend fun unloadModules() = wrappedRunWithoutResult(
        logging = {
            onStart = {
                log.info { "Unloading modules.." }
            }
            onFailure = {
                log.debugExceptionAndInfoMessage(it) { "Unloading modules failed." }
            }
            onSuccess = { measuredMillis, _ ->
                log.info { "Unloading started in $measuredMillis millis." }
            }
        }
    ) {
        modules.forEach { unloadModule(it) }
    }

    private suspend fun unloadModule(module: Module) = wrappedRunWithoutResult {
        module.unload()
        remModule(module)
    }

    override suspend fun onStop() {
        stopModules()
        unloadModules()
    }
}
