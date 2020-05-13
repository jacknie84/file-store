package com.jacknie.filestore.sftp

import com.jacknie.filestore.FileStoreSession
import com.jacknie.filestore.model.FileDirectory
import com.jacknie.filestore.model.FilePath
import com.jcraft.jsch.ChannelSftp
import com.jcraft.jsch.Session
import com.jcraft.jsch.SftpException
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream

class SftpFileStoreSession(
        private val fileStoreDir: FileDirectory,
        private val session: Session,
        private val channel: ChannelSftp
): FileStoreSession {

    override fun exists(filePath: FilePath) = exists("$filePath")

    override fun save(filePath: FilePath, content: InputStream) {
        val dst = fileStoreDir.resolve(filePath).toString()
        try {
            mkdir(filePath.directory)
            content.use { channel.put(it, dst) }
        } catch (e: SftpException) {
            throw handleSftpException(e, dst)
        }
    }

    override fun getFileContent(filePath: FilePath): InputStream {
        var src = fileStoreDir.resolve(filePath).toString()
        try {
            return channel.get(src)
        } catch (e: SftpException) {
            throw handleSftpException(e, src)
        }
    }

    override fun delete(filePath: FilePath) {
        var path = fileStoreDir.resolve(filePath).toString()
        try {
            channel.rm(path)
        } catch (e: SftpException) {
            throw handleSftpException(e, path)
        }
    }

    override fun close() {
        if (!channel.isClosed) {
            channel.disconnect()
        }
        if (session.isConnected) {
            session.disconnect()
        }
    }

    private fun exists(filePath: String): Boolean {
        return try {
            channel.lstat(filePath)
            true
        } catch (e: SftpException) {
            if (ChannelSftp.SSH_FX_NO_SUCH_FILE == e.id) {
                false
            } else {
                throw IOException(e)
            }
        }
    }

    private fun mkdir(fileDir: FileDirectory) {
        val fileStoreDir = this.fileStoreDir.resolve(fileDir)
        var currentDir: FileDirectory? = null
        fileStoreDir.forEach {
            currentDir = currentDir?.resolve(it)?: it
            val path = currentDir.toString()
            if (!exists(path)) {
                try {
                    channel.mkdir(path)
                } catch (e: SftpException) {
                    throw handleSftpException(e, path)
                }
            }
        }
    }

    private fun handleSftpException(e: SftpException, target: String): IOException {
        if (ChannelSftp.SSH_FX_NO_SUCH_FILE == e.id) {
            throw FileNotFoundException(target)
        } else {
            throw IOException(e)
        }
    }
}