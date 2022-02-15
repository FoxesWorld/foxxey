package ru.foxesworld.foxxey.commands

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

/**
 * @author vie10
 **/
internal class BaseCommandTest : BehaviorSpec({

    given("implementation without overrides") {
        `when`("executes") {
            then("returns help") {
                val help = "some help"
                val implementation = ImplementationWithoutOverrides(help = help)
                implementation.execute().getOrThrow().shouldBe(help)
            }
        }
    }
}) {

    class ImplementationWithoutOverrides(
        name: String = "",
        description: String = "",
        group: String = "",
        help: String = ""
    ) : BaseCommand(name, description, group, help)
}
