package ru.foxesworld.foxxey.config

import java.io.File

class FolderConfigFileProvider(
    private val folder: File
) : ConfigFileProvider {

    override fun provide(configInfo: ConfigInfo): Result<ConfigFile> = runCatching {
        val configPath = if (configInfo.group.isEmpty()) {
            configInfo.name
        } else "${configInfo.group}/${configInfo.name}"
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
            file.writeBytes(byteArray)
        }

        override fun exists(): Boolean = file.exists()
    }
}
