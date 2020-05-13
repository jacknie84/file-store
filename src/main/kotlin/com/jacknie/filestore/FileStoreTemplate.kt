package com.jacknie.filestore

import com.jacknie.filestore.model.FilePath
import com.jacknie.filestore.model.UploadPolicy
import com.jacknie.filestore.model.UploadSource
import java.io.IOException
import java.io.InputStream

class FileStoreTemplate<S: FileStoreSession>(
        private val factory: FileStoreSessionFactory<S>,
        private val validate: UploadValidator
) {

    @Throws(IOException::class, UploadPolicyException::class)
    fun save(source: UploadSource, policy: UploadPolicy, locate: FileStoreLocator): FilePath {
        validate(source, policy)
        return factory.create().use {
            val filePath = locate(it)
            it.save(filePath, source.content)
            filePath
        }
    }

    @Throws(IOException::class)
    fun load(filePath: FilePath): InputStream {
        return factory.create().use { it.load(filePath) }
    }

    @Throws(IOException::class)
    fun delete(filePath: FilePath) {
        factory.create().use { it.delete(filePath) }
    }
}