package ru.foxesworld.foxxey.config

import kotlin.reflect.KProperty
import kotlin.reflect.full.instanceParameter

/**
 * @author vie10
 **/
internal val KProperty<*>.systemKey: String
    get() = "${instanceParameter!!.type}$name"

@Suppress("UNCHECKED_CAST")
fun <T> KProperty<T>.fromSystem(): T = System.getProperties()[systemKey] as T
