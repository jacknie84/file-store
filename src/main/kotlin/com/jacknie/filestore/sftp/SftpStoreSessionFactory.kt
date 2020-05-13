package com.jacknie.filestore.sftp

import com.jacknie.filestore.FileStoreSessionFactory
import com.jacknie.filestore.model.FileDirectory
import com.jacknie.filestore.model.FilePath
import com.jcraft.jsch.*
import org.slf4j.LoggerFactory

class SftpStoreSessionFactory(
        private val storeFileDir: FileDirectory,
        private val sftp: Sftp
): FileStoreSessionFactory<SftpFileStoreSession> {

    constructor(storeFileDir: String, sftp: Sftp): this(FileDirectory(storeFileDir), sftp)

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun create(): SftpFileStoreSession {
        val host = sftp.host
        val user = sftp.user
        val port = if (sftp.port <= 0) 22 else sftp.port

        logger.info("sftp: host={}, port={}, user={}", host, port, user)

        // prepare JSch
        val jSch = JSch()
        val privateKey = sftp.privateKey?.use { it.readBytes() }
        val passphrase = sftp.userInfo.passphrase?.toByteArray()
        sftp.knownHosts?.also { jSch.setKnownHosts(it) }
        privateKey?.also { jSch.addIdentity(user, it, null, passphrase) }

        // prepare JSch session
        val session = jSch.getSession(user, host, port)
        session.setPassword(sftp.userInfo.password)
        session.userInfo = sftp.userInfo
        sftp.proxy?.let { toProxy(it) }?.also { session.setProxy(it) }
        sftp.timeout?.also { session.timeout = it }
        sftp.serverAliveInterval?.also { session.serverAliveInterval = it }
        sftp.socketFactory?.also { session.setSocketFactory(it) }
        sftp.clientVersion?.also { session.clientVersion = it }
        sftp.hostKeyAlias?.also { session.hostKeyAlias = it }
        sftp.serverAliveCountMax?.also { session.serverAliveCountMax = it }
        sftp.daemonThread?.also { session.setDaemonThread(it) }
        session.connect()

        // open channel sftp
        val channel = session.openChannel("sftp") as ChannelSftp
        channel.connect(sftp.channelConnectTimeout.toMillis().toInt())
        logger.info("sftp connected: pwd={}", channel.pwd())

        return SftpFileStoreSession(storeFileDir, session, channel)
    }

    private fun toProxy(proxy: SftpProxy): Proxy {
        return when (proxy) {
            SftpProxy.HTTP -> createProxyHTTP()
            SftpProxy.SOCKS4 -> createProxySOCKS4()
            SftpProxy.SOCKS5 -> createProxySOCKS5()
        }
    }

    private fun createProxyHTTP(): ProxyHTTP {
        val proxy = ProxyHTTP(sftp.host, sftp.port)
        proxy.setUserPasswd(sftp.user, sftp.userInfo.password)
        return proxy
    }

    private fun createProxySOCKS4(): ProxySOCKS4 {
        val proxy = ProxySOCKS4(sftp.host, sftp.port)
        proxy.setUserPasswd(sftp.user, sftp.userInfo.password)
        return proxy
    }

    private fun createProxySOCKS5(): ProxySOCKS5 {
        val proxy = ProxySOCKS5(sftp.host, sftp.port)
        proxy.setUserPasswd(sftp.user, sftp.userInfo.password)
        return proxy
    }
}