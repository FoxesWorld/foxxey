package ru.foxesworld.foxxey.modules

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mu.KotlinLogging
import ru.foxesworld.foxxey.commands.Command
import ru.foxesworld.foxxey.config.ConfigInfo
import ru.foxesworld.foxxey.logging.wrappedRun
import java.io.File
import java.util.*
import java.util.jar.JarEntry
import java.util.jar.JarFile
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.jvm.jvmName

private val log = KotlinLogging.logger { }

/**
 * @author vie10
 **/
class JarModuleSource(
    private val classLoaderImpl: JarClassLoader,
    private val file: File
) : ModuleSource {

    override suspend fun importModule(): Result<Module> = log.wrappedRun(
        target = "module from file ${file.name}",
        verb = "import"
    ) {
        val jar = withContext(Dispatchers.IO) {
            classLoaderImpl.addJar(file)
            JarFile(file)
        }

        val allClasses = jar.entries().toKClassList()
        val module: Module = allClasses.findModule().getOrThrow()
        log.debug { "Found module $module in jar $file" }
        allClasses.findCommands().getOrThrow().forEach {
            log.debug { "Found command $it in module $module" }
            module.addCommand(it)
        }
        allClasses.findConfig().getOrThrow().forEach {
            log.debug { "Found config $it in module $module" }
            module.addConfigInfo(it)
        }

        module
    }

    private fun Enumeration<JarEntry>.toKClassList(): List<KClass<out Any>> = toList().filter {
        it.name.endsWith(".class")
    }.map {
        val jvmName = it.name.replace("/", ".").substringBefore(".class")
        classLoaderImpl.findClassInJars(jvmName)
    }

    private fun List<KClass<out Any>>.findModule(): Result<Module> = runCatching {
        find { it.isSubclassOf(Module::class) }!!.createInstance() as Module
    }

    private fun List<KClass<out Any>>.findCommands(): Result<List<Command>> = runCatching {
        filter { it.isSubclassOf(Command::class) }.map { it.createInstance() as Command }
    }

    private fun List<KClass<out Any>>.findConfig(): Result<List<ConfigInfo>> = runCatching {
        filter { it.hasAnnotation<Config>() }.map {
            val annotation: Config = it.findAnnotation()!!
            ConfigInfo(annotation.name, annotation.group, it.jvmName)
        }
    }
}
