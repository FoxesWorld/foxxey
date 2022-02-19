package ru.foxesworld.foxxey.config

class ConfigWriterImpl(
    private val userConfigFileProvider: ConfigFileProvider,
    private val defaultConfigFileProvider: ConfigFileProvider
) : ConfigWriter {
    
    override fun write(configInfo: ConfigInfo, byteArray: ByteArray) {
        val userConfigFile = userConfigFileProvider.provide(configInfo).getOrElse { return }
        userConfigFile.write(byteArray)
    }

    override fun writeDefaultIfNotExists(configInfo: ConfigInfo) {
        val userConfigFile = userConfigFileProvider.provide(configInfo).getOrElse { return }
        val defaultConfigFile = defaultConfigFileProvider.provide(configInfo).getOrElse { return }
        if (defaultConfigFile.exists()) {
            defaultConfigFile.read().onSuccess { userConfigFile.write(it) }
        }
    }
}
