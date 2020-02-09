package com.jacknie.fd.sftp

import com.jacknie.fd.FileStore
import com.jcraft.jsch.ChannelSftp
import com.jcraft.jsch.SftpException
import java.io.IOException
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.nio.file.StandardOpenOption

class SftpFileStore: FileStore<SftpFileStoreSession> {

    override fun exists(session: SftpFileStoreSession, path: String, filename: String): Boolean {
        return exists(session.channel, "$path/$filename")
    }

    override fun save(session: SftpFileStoreSession, path: String, filename: String, content: InputStream) {
        try {
            mkdir(session.channel, path)
            content.use { session.channel.put(it, "/$path/$filename") }
        } catch (e: SftpException) {
            throw IOException(e)
        }
    }

    override fun load(session: SftpFileStoreSession, path: String, filename: String): InputStream {
        try {
            val source = session.channel.get("/$path/$filename")
            val tempFile = createTempFile()
            source.use { Files.copy(it, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING) }
            return Files.newInputStream(tempFile.toPath(), StandardOpenOption.DELETE_ON_CLOSE)
        } catch (e: SftpException) {
            throw IOException(e)
        }
    }

    override fun delete(session: SftpFileStoreSession, path: String, filename: String) {
        try {
            session.channel.rm("/$path/$filename")
        } catch (e: SftpException) {
            throw IOException(e)
        }
    }

    @Throws(IOException::class)
    private fun exists(channel: ChannelSftp, path: String): Boolean {
        return try {
            channel.lstat(path)
            true
        } catch (e: SftpException) {
            if (ChannelSftp.SSH_FX_NO_SUCH_FILE == e.id) {
                false
            } else {
                throw IOException(e)
            }
        }
    }

    private fun mkdir(channel: ChannelSftp, path: String) {
        var current = arrayListOf<String>()
        Paths.get(path)
                .map { it.toString() }
                .filter { it.isNotBlank() }
                .forEach {
                    current.add(it)
                    val currentPath = "/${current.joinToString("/")}"
                    if (!exists(channel, currentPath)) {
                        channel.mkdir(currentPath)
                    }
                }
    }
}