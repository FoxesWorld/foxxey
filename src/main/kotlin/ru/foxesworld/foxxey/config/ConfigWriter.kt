package ru.foxesworld.foxxey.config

/**
 * @author vie10
 **/
interface ConfigWriter {

    fun write(configInfo: ConfigInfo, byteArray: ByteArray)

    fun writeDefaultIfNotExists(configInfo: ConfigInfo)
}
