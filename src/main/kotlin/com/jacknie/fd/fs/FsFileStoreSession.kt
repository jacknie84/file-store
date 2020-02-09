package com.jacknie.fd.fs

import com.jacknie.fd.FileStoreSession
import java.io.File
import java.nio.file.FileSystem
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path

class FsFileStoreSession(private val fs: FileSystem): FileStoreSession {

    override fun close() {
        // do nothing...
    }

    fun getPath(path: String, filename: String): Path {
        val dir = Files.createDirectories(fs.getPath(path))
        return dir.resolve(filename)
    }

}