package com.jacknie.filestore.filesystem

import com.jacknie.filestore.FileStoreSessionFactory
import com.jacknie.filestore.model.FileDirectory
import java.nio.file.FileSystem
import java.nio.file.FileSystems

class FileSystemStoreSessionFactory(
        private val storeFileDir: FileDirectory,
        private val fileSystem: FileSystem = FileSystems.getDefault()
): FileStoreSessionFactory<FileSystemStoreSession> {

    constructor(storeFileDir: String, fileSystem: FileSystem): this(FileDirectory(storeFileDir), fileSystem)

    override fun create() = FileSystemStoreSession(storeFileDir, fileSystem)
}