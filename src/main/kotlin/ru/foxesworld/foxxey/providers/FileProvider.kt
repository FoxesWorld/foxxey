package ru.foxesworld.foxxey.providers

import java.io.File

/**
 * @author vie10
 **/
fun interface FileProvider {

    fun provide(path: String): File
}
