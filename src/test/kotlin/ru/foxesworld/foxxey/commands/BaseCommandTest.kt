package ru.foxesworld.foxxey.commands

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * @author vie10
 */
internal class BaseCommandTest {

    @Test
    fun `GIVEN implementation without overrides WHEN execute THEN help is returned`() {
        val help = "some help"
        val implementation = new(help = help)
        assertTrue {
            implementation.execute().getOrNull() == help
        }
    }

    private fun new(name: String = "", description: String = "", group: String = "", help: String = ""): BaseCommand {
        return object : BaseCommand(name, description, group, help) {}
    }
}
