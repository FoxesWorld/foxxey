package ru.foxesworld.foxxey.config

import kotlinx.serialization.SerialName
import kotlin.reflect.KClass

/**
 * @author vie10
 **/
data class ConfigInfo(
    @SerialName("name")
    val name: String,
    @SerialName("group")
    val group: String,
    @SerialName("className")
    val className: String
) {

    internal val clazz: KClass<out Any> by lazy {
        Class.forName(className).kotlin
    }
}
