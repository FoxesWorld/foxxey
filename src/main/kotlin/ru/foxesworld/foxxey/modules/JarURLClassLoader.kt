package ru.foxesworld.foxxey.modules

import java.io.File
import java.net.URLClassLoader
import kotlin.reflect.KClass

/**
 * @author vie10
 **/
class JarURLClassLoader(parent: ClassLoader) : URLClassLoader(
    "Jar modules class loader", emptyArray(), parent
), JarClassLoader {

    override fun addJar(file: File) {
        addURL(file.toURI().toURL())
    }

    override fun findClassInJars(name: String): KClass<out Any> = findClass(name).kotlin
}
