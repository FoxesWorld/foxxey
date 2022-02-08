package ru.foxesworld.foxxey.config

import com.sksamuel.hoplite.ConfigAlias
import kotlin.reflect.KClass

/**
 * @author vie10
 **/
data class ConfigInfo(
    @ConfigAlias("group")
    val group: String = "",
    @ConfigAlias("name")
    val name: String,
    @ConfigAlias("class")
    val className: String
) {

    internal val clazz: KClass<out Any>
        get() = Class.forName(className).kotlin
}
