package ru.foxesworld.foxxey.modules

/**
 * @author vie10
 **/
interface ModuleSource {

    suspend fun importModule(): Result<Module>
}
