package ru.foxesworld.foxxey

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.koin.core.context.startKoin
import ru.foxesworld.foxxey.di.Modules

/**
 * @author vie10
 **/

@DelicateCoroutinesApi
fun main() {
    val app = startKoin {
        modules(Modules.release())
    }

    val foxxey: Foxxey = app.koin.get()
    runBlocking {
        foxxey.scope.launch {
            foxxey.start()
        }.join()
    }
}
