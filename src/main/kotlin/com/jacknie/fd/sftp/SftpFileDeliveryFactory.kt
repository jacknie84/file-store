package com.jacknie.fd.sftp

import com.jacknie.fd.FileDelivery
import com.jacknie.fd.FileDeliveryFactory
import com.jcraft.jsch.*

class SftpFileDeliveryFactory(private val sftp: Sftp): FileDeliveryFactory<SftpFileStoreSession> {

    override fun createFileDelivery(): FileDelivery<SftpFileStoreSession> {
        val jSch = JSch()
        val user = sftp.user
        val host = sftp.host
        val port = if (sftp.port <= 0) 22 else sftp.port
        val privateKey = sftp.privateKey?.use { it.readBytes() }
        val passphrase = sftp.userInfo.passphrase?.toByteArray()
        val proxy = sftp.proxy?.run { toProxy() }
        sftp.knownHosts?.also { jSch.setKnownHosts(it) }
        privateKey?.also { jSch.addIdentity(user, it, null, passphrase) }
        val jSchSession = jSch.getSession(user, host, port)
        sftp.config?.forEach { jSchSession.setConfig(it.key, it.value) }
        jSchSession.setPassword(sftp.userInfo.password)
        jSchSession.userInfo = sftp.userInfo
        jSchSession.setProxy(proxy)
        sftp.timeout?.also { jSchSession.timeout = it }
        sftp.serverAliveInterval?.also { jSchSession.serverAliveInterval = it }
        sftp.socketFactory?.also { jSchSession.setSocketFactory(it) }
        sftp.clientVersion?.also { jSchSession.clientVersion = it }
        sftp.hostKeyAlias?.also { jSchSession.hostKeyAlias = it }
        sftp.serverAliveCountMax?.also { jSchSession.serverAliveCountMax = it }
        sftp.daemonThread?.also { jSchSession.setDaemonThread(it) }
        val session = SftpFileStoreSession(jSchSession, sftp.channelConnectTimeout)
        return FileDelivery(SftpFileStore(), session)
    }

    private fun toProxy(): Proxy {
        return when (sftp.proxy) {
            SftpProxy.HTTP -> createProxyHTTP()
            SftpProxy.SOCKS4 -> createProxySOCKS4()
            SftpProxy.SOCKS5 -> createProxySOCKS5()
            else -> throw IllegalStateException("unsupported sftp proxy type: " + sftp.proxy)
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
