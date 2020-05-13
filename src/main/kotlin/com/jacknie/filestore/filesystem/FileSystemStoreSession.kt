package com.jacknie.filestore.filesystem

import com.jacknie.filestore.FileStoreSession
import com.jacknie.filestore.model.FileDirectory
import com.jacknie.filestore.model.FilePath
import java.io.InputStream
import java.lang.UnsupportedOperationException
import java.nio.file.FileSystem
import java.nio.file.Files
import java.nio.file.Path

class FileSystemStoreSession(
        private val fileStoreDir: FileDirectory,
        private val fileSystem: FileSystem
): FileStoreSession {

    override fun exists(filePath: FilePath): Boolean {
        val sysFilePath = toFileStorePath(filePath)
        return sysFilePath.toFile().exists()
    }

    override fun save(filePath: FilePath, content: InputStream) {
        val sysFilePath = toFileStorePath(filePath)
        Files.createDirectories(sysFilePath.parent)
        Files.copy(content, sysFilePath)
    }

    override fun getFileContent(filePath: FilePath): InputStream {
        val sysFilePath = toFileStorePath(filePath)
        return Files.newInputStream(sysFilePath)
    }

    override fun delete(filePath: FilePath) {
        val sysFilePath = toFileStorePath(filePath)
        Files.deleteIfExists(sysFilePath)
    }

    override fun close() {
        try {
            fileSystem.close()
        } catch (e: UnsupportedOperationException) {
            // do nothing...
        }
    }

    private fun toFileStorePath(filePath: FilePath): Path {
        val pathNames = filePath.directory.pathNames.toTypedArray()
        val filename = filePath.filename.toString()
        return fileSystem.getPath(fileStoreDir.toString(), *pathNames, filename)
    }
}