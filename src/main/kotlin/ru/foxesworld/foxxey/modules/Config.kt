package ru.foxesworld.foxxey.modules

/**
 * @author vie10
 **/
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class Config(val name: String, val group: String)
