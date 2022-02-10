package ru.foxesworld.foxxey.commands

/**
 * @author vie10
 **/
@Suppress("MemberVisibilityCanBePrivate")
abstract class BaseCommand(
    override val name: String,
    override val description: String,
    override val group: String,
    val help: String
) : Command {

    override fun execute(): Result<String> = runCatching {
        help
    }
}
