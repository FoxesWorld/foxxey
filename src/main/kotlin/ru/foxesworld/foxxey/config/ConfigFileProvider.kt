package ru.foxesworld.foxxey.config

/**
 * @author vie10
 **/
interface ConfigFileProvider {

    fun provide(configInfo: ConfigInfo): Result<ConfigFile>
}
