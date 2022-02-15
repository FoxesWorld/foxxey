package ru.foxesworld.foxxey.modules

import java.io.File
import kotlin.reflect.KClass

/**
 * @author vie10
 **/
interface JarClassLoader {

    fun addJar(file: File)

    fun findClassInJars(name: String): KClass<out Any>
}
