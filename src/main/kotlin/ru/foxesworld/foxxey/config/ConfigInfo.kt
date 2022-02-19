package ru.foxesworld.foxxey.config

import kotlinx.serialization.SerialName
import kotlin.reflect.KClass

/**
 * @author vie10
 **/
data class ConfigInfo(
    @SerialName("name")
    val name: String,
    @SerialName("className")
    val className: String,
    @SerialName("group")
    val group: String = NO_GROUP,
    @SerialName("extension")
    val extension: String = "json"
) {

    val nameWithExtension: String by lazy { "$name.$extension" }
    internal val clazz: KClass<out Any> by lazy {
        Class.forName(className).kotlin
    }

    companion object {

        const val NO_GROUP = ""
    }
}
