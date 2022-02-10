package ru.foxesworld.foxxey.config.hoplite

import com.sksamuel.hoplite.addStreamSource
import ru.foxesworld.foxxey.config.ConfigFileProvider
import ru.foxesworld.foxxey.config.ConfigInfo
import ru.foxesworld.foxxey.config.ConfigLoader
import com.sksamuel.hoplite.ConfigLoader as HopliteConfigLoader

/**
 * @author vie10
 **/
class HopliteConfigLoader(
    private val configFileProvider: ConfigFileProvider,
    private val extension: String
) : ConfigLoader {

    override fun load(configInfo: ConfigInfo): Result<Any> = runCatching {
        val configFile = configFileProvider.provide(configInfo).getOrThrow()
        val configBytes = configFile.read().getOrThrow()
        HopliteConfigLoader.Builder()
            .addStreamSource(configBytes.inputStream(), extension)
            .build()
            .loadConfigOrThrow(configInfo.clazz, emptyList())
    }
}
