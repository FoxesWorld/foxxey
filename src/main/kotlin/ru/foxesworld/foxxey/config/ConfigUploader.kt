package ru.foxesworld.foxxey.config

/**
 * @author vie10
 **/
interface ConfigUploader {

    fun uploadToSystem(configInfo: ConfigInfo)
}
