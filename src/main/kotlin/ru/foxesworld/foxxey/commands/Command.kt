package ru.foxesworld.foxxey.commands

import org.koin.core.component.KoinComponent

/**
 * @author vie10
 **/
interface Command : KoinComponent {

    val name: String
    val description: String
    val group: String

    fun execute(): Result<String>

    @Target(AnnotationTarget.VALUE_PARAMETER)
    @Retention(AnnotationRetention.RUNTIME)
    annotation class Param(
        val name: String,
        val description: String
    )
}
