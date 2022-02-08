package ru.foxesworld.foxxey.config

import kotlin.reflect.full.memberProperties

/**
 * @author vie10
 **/
abstract class BaseConfigUploader : ConfigUploader {

    final override fun uploadToSystem(configInfo: ConfigInfo) {
        val modelClass = configInfo.clazz
        val model = loadOrCreateDefaultModel(configInfo).getOrThrow()
        val properties = System.getProperties()
        modelClass.memberProperties.forEach { property ->
            properties[property.systemKey] = property.call(model)
        }
    }

    abstract fun loadOrCreateDefaultModel(configInfo: ConfigInfo): Result<Any>
}
