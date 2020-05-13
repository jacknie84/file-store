package com.jacknie.filestore.sftp

import com.jcraft.jsch.SocketFactory
import com.jcraft.jsch.UserInfo
import java.io.InputStream
import java.time.Duration

data class Sftp(
        var host: String,
        var user: String,
        var port: Int = 22,
        var channelConnectTimeout: Duration = Duration.ofSeconds(5),
        var userInfo: UserInfo,
        var knownHosts: String? = null,
        var privateKey: InputStream? = null,
        var config: Map<String, String>? = null,
        var timeout: Int? = null,
        var serverAliveInterval: Int? = null,
        var proxy: SftpProxy? = null,
        var socketFactory: SocketFactory? = null,
        var clientVersion: String? = null,
        var hostKeyAlias: String? = null,
        var serverAliveCountMax: Int? = null,
        var daemonThread: Boolean? = null
) {
    constructor(host: String, user: String, password: String): this(
            host = host,
            user = user,
            userInfo = SimpleUserInfo(password)
    )
}

enum class SftpProxy {
    HTTP, SOCKS4, SOCKS5
}

class SimpleUserInfo(private val passphrase: String?, private val password: String?): UserInfo {

    constructor(password: String): this(null, password)

    override fun getPassphrase(): String? = passphrase

    override fun getPassword(): String? = password

    override fun promptPassphrase(message: String?): Boolean = false

    override fun promptYesNo(message: String?): Boolean = true

    override fun showMessage(message: String?) {}

    override fun promptPassword(message: String?): Boolean = false
}