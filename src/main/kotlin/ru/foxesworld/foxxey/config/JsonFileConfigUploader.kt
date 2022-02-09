package ru.foxesworld.foxxey.config

import com.sksamuel.hoplite.ConfigLoader
import com.sksamuel.hoplite.addFileSource
import com.sksamuel.hoplite.addResourceSource
import ru.foxesworld.foxxey.providers.FileProvider
import java.io.File
import kotlin.reflect.KClass

private const val CONFIG_FOLDER_NAME: String = "config"
private const val CONFIG_FORMAT: String = "json"

/**
 * @author vie10
 **/
class JsonFileConfigUploader(
    private val configFileProvider: FileProvider = FileProvider {
        File(CONFIG_FOLDER_NAME, it)
    },
    private val defaultConfigFileProvider: FileProvider = FileProvider {
        File(JsonFileConfigUploader::class.java.getResource("/$it")!!.file)
    }
) : BaseConfigUploader() {

    private val ConfigInfo.file: File
        get() = configFileProvider.provide(path)
    private val File.defaultFile: File
        get() = defaultConfigFileProvider.provide(path)
    private val ConfigInfo.path: String
        get() = "$CONFIG_FOLDER_NAME/$group/$name.$CONFIG_FORMAT".replace("//", "/")

    override fun loadOrCreateDefaultModel(configInfo: ConfigInfo): Result<Any> = runCatching {
        val configFile = configInfo.file
        configFile.writeDefaultIfNotExists()
        configInfo.clazz.fromFileOrThrow(configFile)
    }

    private fun File.writeDefaultIfNotExists() {
        if (exists()) {
            return
        }
        val defaultConfigFile = defaultFile
        if (defaultConfigFile.exists()) {
            parentFile.mkdirs()
            createNewFile()
            writeBytes(defaultConfigFile.readBytes())
        }
    }

    private fun <T : Any> KClass<T>.fromFileOrThrow(file: File): T {
        return ConfigLoader.Builder()
            .addFileSource(file, optional = true)
            .addResourceSource("/${file.path}")
            .build().loadConfigOrThrow(this, emptyList())
    }
}
