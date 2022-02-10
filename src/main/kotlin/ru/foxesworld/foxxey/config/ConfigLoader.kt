package ru.foxesworld.foxxey.config

/**
 * @author vie10
 **/
interface ConfigLoader {

    fun load(configInfo: ConfigInfo): Result<Any>
}
