package ru.foxesworld.foxxey.config

import com.sksamuel.hoplite.ConfigAlias
import kotlin.reflect.KClass

/**
 * @author vie10
 **/
data class ConfigInfo(
    @ConfigAlias("name")
    val name: String,
    @ConfigAlias("group")
    val group: String,
    @ConfigAlias("className")
    val className: String
) {

    internal val clazz: KClass<out Any> by lazy {
        Class.forName(className).kotlin
    }
}
