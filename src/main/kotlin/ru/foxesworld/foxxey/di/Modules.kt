package ru.foxesworld.foxxey.di

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.newFixedThreadPoolContext
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import org.koin.dsl.bind
import org.koin.dsl.module
import ru.foxesworld.foxxey.Foxxey
import ru.foxesworld.foxxey.FoxxeyBaseImpl
import ru.foxesworld.foxxey.config.*
import ru.foxesworld.foxxey.config.hoplite.HopliteConfigLoader
import java.io.File
import org.koin.core.module.Module as KoinModule

/**
 * @author vie10
 **/
@DelicateCoroutinesApi
@Suppress("MemberVisibilityCanBePrivate")
object Modules {

    private val Scope.jarResourcesConfigFolderName: String
        get() = getProperty("jar_resources_config_folder_name", "config")
    private val Scope.jarResourcesConfigExtension: String
        get() = getProperty("jar_resources_config_extension", "json")
    private val Scope.userConfigFolderName: String
        get() = getProperty("user_config_folder_name", "config")
    private val Scope.userConfigExtension: String
        get() = getProperty("user_config_extension", "json")

    fun release() = module {
        foxxey()
        config()
        hopliteConfigLoaders()
    }

    val foxxey: KoinModule.() -> Unit = {
        single {
            val configLoader: ConfigLoader = get(named(ConfigLoader.Type.Default))
            configLoader.load(Foxxey.BuildInfo.info).getOrThrow() as Foxxey.BuildInfo
        }
        single {
            FoxxeyBaseImpl()
        } bind Foxxey::class
        single {
            val configInfo = Foxxey.Config.info
            val configWriter: ConfigWriter = get()
            configWriter.writeDefaultIfNotExists(configInfo)
            val configLoader: ConfigLoader = get()
            configLoader.load(configInfo).getOrThrow()
        } bind Foxxey.Config::class
        single(named(Foxxey.COROUTINE_SCOPE_NAME)) {
            val config: Foxxey.Config = get()
            val coroutineContext = newFixedThreadPoolContext(config.threadsCount, Foxxey.COROUTINE_SCOPE_NAME)
            CoroutineScope(coroutineContext)
        } bind CoroutineScope::class
    }

    val config: KoinModule.() -> Unit = {
        configFileProviders()
        configWriters()
    }

    val configWriters: KoinModule.() -> Unit = {
        single {
            val defaultConfigFileProvider: ConfigFileProvider = get(named(ConfigFileProvider.Source.Jar))
            val userConfigFileProvider: ConfigFileProvider = get()
            ConfigWriterImpl(userConfigFileProvider, defaultConfigFileProvider)
        } bind ConfigWriter::class
    }

    val configFileProviders: KoinModule.() -> Unit = {
        single(named(ConfigFileProvider.Source.Jar)) {
            val jarResourcesConfigFolder =
                File(Modules::class.java.getResource("/$jarResourcesConfigFolderName")!!.file)
            FolderConfigFileProvider(jarResourcesConfigFolder)
        } bind ConfigFileProvider::class
        single {
            val userConfigFolder = File(userConfigFolderName)
            FolderConfigFileProvider(userConfigFolder)
        } bind ConfigFileProvider::class
    }

    val hopliteConfigLoaders: KoinModule.() -> Unit = {
        single(named(ConfigLoader.Type.Default)) {
            val configFileProvider: ConfigFileProvider = get(named(ConfigFileProvider.Source.Jar))
            HopliteConfigLoader(configFileProvider, jarResourcesConfigExtension)
        } bind ConfigLoader::class

        single() {
            HopliteConfigLoader(get(), userConfigExtension)
        } bind ConfigLoader::class
    }
}
