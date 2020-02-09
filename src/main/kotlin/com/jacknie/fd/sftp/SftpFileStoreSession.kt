package com.jacknie.fd.sftp

import com.jacknie.fd.FileStoreSession
import com.jcraft.jsch.ChannelSftp
import com.jcraft.jsch.Session
import com.jcraft.jsch.SftpException
import org.slf4j.LoggerFactory
import java.lang.IllegalStateException
import java.time.Duration

class SftpFileStoreSession(
        private val session: Session,
        private val channelConnectTimeout: Duration): FileStoreSession {

    private val logger = LoggerFactory.getLogger(javaClass)

    val channel: ChannelSftp by lazy {
        if (!session.isConnected) {
            session.connect()
        }
        val channel = session.openChannel("sftp") as ChannelSftp
        channel.connect(channelConnectTimeout.toMillis().toInt())

        val host = session.host
        val port = session.port
        val user = session.userName
        val pwd = channel.pwd()
        logger.info("sftp connected: host={}, port={}, user={}, pwd={}", host, port, user, pwd)

        channel
    }

    override fun close() {
        if (!channel.isClosed) {
            channel.disconnect()
        }
        if (session.isConnected) {
            session.disconnect()
        }
    }
}