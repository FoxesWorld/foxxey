package ru.foxesworld.foxxey.modules

import java.net.URL

/**
 * @author vie10
 **/
interface JarFile {

    val url: URL
    val classJVMNames: List<String>
}
