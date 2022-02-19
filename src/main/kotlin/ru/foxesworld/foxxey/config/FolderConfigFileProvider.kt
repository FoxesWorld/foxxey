package ru.foxesworld.foxxey.config

import java.io.File

class FolderConfigFileProvider(
    private val folder: File
) : ConfigFileProvider {

    override fun provide(configInfo: ConfigInfo): Result<ConfigFile> = runCatching {
        if (configInfo.name.isEmpty()) {
            throw IllegalStateException("Unable provide config without name")
        }
        val configPath = if (configInfo.group.isEmpty()) {
            configInfo.nameWithExtension
        } else "${configInfo.group}/${configInfo.nameWithExtension}"
        val file = folder.resolve(configPath)
        CommonFileWrap(file)
    }

    private class CommonFileWrap(
        private val file: File
    ) : ConfigFile {

        override val path: String = file.path

        override fun read(): Result<ByteArray> = runCatching {
            file.readBytes()
        }

        override fun write(byteArray: ByteArray) {
            file.parentFile.apply {
                if (!exists()) {
                    mkdirs()
                }
            }
            file.writeBytes(byteArray)
        }

        override fun exists(): Boolean = file.exists()
    }
}
