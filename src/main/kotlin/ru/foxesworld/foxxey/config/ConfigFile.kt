package ru.foxesworld.foxxey.config

/**
 * @author vie10
 **/
interface ConfigFile {

    val path: String

    fun read(): Result<ByteArray>

    fun write(byteArray: ByteArray)

    fun exists(): Boolean
}
