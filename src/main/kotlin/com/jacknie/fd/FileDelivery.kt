package com.jacknie.fd

import java.io.IOException
import java.io.InputStream

class FileDelivery<T : FileStoreSession>(
        private val fileStore: FileStore<T>,
        private val session: T
): AutoCloseable {

    private var closed = false

    @Throws(RejectFileException::class, IOException::class)
    fun put(policy: FilePolicy, source: FileSource): DeliveredFile {
        validateFileSource(policy, source)
        val path = policy.getSystemPath(source)
        val filename = getFilename(policy, fileStore, path, source, session)
        return saveFileSource(fileStore, path, filename, source, session)
    }

    @Throws(IOException::class)
    fun get(file: DeliveredFile): InputStream {
        return fileStore.load(session, file.path, file.filename)
    }

    @Throws(IOException::class)
    fun delete(file: DeliveredFile) {
        fileStore.delete(session, file.path, file.filename)
    }

    override fun close() {
        if (closed) {
            throw IllegalStateException("already closed")
        }
        else {
            session.close()
            closed = true
        }
    }

    private fun validateFileSource(policy: FilePolicy, source: FileSource) {
        val verification = policy.verifyFileSource(source)
        if (verification.rejects.isNotEmpty()) {
            throw RejectFileException(verification)
        }
    }

    private fun saveFileSource(fileStore: FileStore<T>, path: String, filename: String, source: FileSource, session: T): DeliveredFile {
        fileStore.save(session, path, filename, source.content)
        return DeliveredFile(path, filename, source.extension, source.mimeType, source.fileSize, source.filename)
    }

    private fun getFilename(policy: FilePolicy, fileStore: FileStore<T>, path: String, source: FileSource, session: T): String {
        return if (source.overwrite) {
            policy.generateFilename(source)
        } else {
            getUniqueFilename(policy, fileStore, path, source, session)
        }
    }

    private fun getUniqueFilename(policy: FilePolicy, fileStore: FileStore<T>, path: String, source: FileSource, session: T): String {
        var filename: String
        do {
            filename = policy.generateFilename(source)
        } while (fileStore.exists(session, path, filename))
        return filename
    }
}