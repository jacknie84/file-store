package com.jacknie.fd.fs

import com.jacknie.fd.FileStore
import java.io.InputStream
import java.nio.file.*

class FsFileStore: FileStore<FsFileStoreSession> {

    override fun exists(session: FsFileStoreSession, path: String, filename: String): Boolean {
        val dest = session.getPath(path, filename)
        return Files.exists(dest, LinkOption.NOFOLLOW_LINKS)
    }

    override fun save(session: FsFileStoreSession, path: String, filename: String, content: InputStream) {
        val dest = session.getPath(path, filename)
        Files.copy(content, dest, StandardCopyOption.REPLACE_EXISTING)
    }

    override fun load(session: FsFileStoreSession, path: String, filename: String): InputStream {
        val dest = session.getPath(path, filename)
        return Files.newInputStream(dest, StandardOpenOption.READ)
    }

    override fun delete(session: FsFileStoreSession, path: String, filename: String) {
        val dest = session.getPath(path, filename)
        Files.deleteIfExists(dest)
    }
}