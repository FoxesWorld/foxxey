package ru.foxesworld.foxxey.modules

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import java.io.File
import java.net.URL
import java.util.jar.JarFile as JavaJarFile

/**
 * @author vie10
 **/
class JarFileImpl(private val file: File) : JarFile {

    override val url: URL by lazy { file.toURI().toURL() }
    override val classJVMNames: List<String> = runBlocking(Dispatchers.IO) {
        JavaJarFile(file).entries().toList().filter { it.name.endsWith(".class") }.map {
            it.name.replace("/", ".").substringBefore(".class")
        }
    }

    override fun toString(): String {
        return file.toString()
    }
}
