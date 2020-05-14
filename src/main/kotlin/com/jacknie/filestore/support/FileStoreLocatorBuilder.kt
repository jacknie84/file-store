package com.jacknie.filestore.support

import com.jacknie.filestore.FileStoreLocator
import com.jacknie.filestore.FileStoreSession
import com.jacknie.filestore.model.FileDirectory
import com.jacknie.filestore.model.FilePath
import com.jacknie.filestore.model.Filename

data class FileStoreLocatorBuilder(
        private var overwrite: Boolean = false,
        private var naming: (() -> Filename)? = null,
        private var uploadDir: FileDirectory? = null,
        private var filename: Filename? = null,
        private var tryLimit: Int = 10
) {
    fun overwrite() = apply { this.overwrite = true }
    fun naming(naming: () -> Filename) = apply { this.naming = naming }
    fun uploadDir(uploadDir: FileDirectory) = apply { this.uploadDir = uploadDir }
    fun filename(filename: Filename) = apply { this.filename = filename }
    fun tryLimit(tryLimit: Int) = apply { this.tryLimit = tryLimit }

    fun build(): FileStoreLocator {
        return if (naming != null) {
            { locateUnique(uploadDir!!, naming!!, it) }
        } else {
            { locateSimple(uploadDir!!, it) }
        }
    }

    private fun locateUnique(uploadDir: FileDirectory, naming: () -> Filename, session: FileStoreSession): FilePath {
        var tryCount = 0
        var filePath: FilePath
        do {
            if (tryCount > tryLimit) {
                throw IllegalStateException("The try limit($tryLimit) has been exceeded.")
            }
            tryCount++
            filePath = FilePath(uploadDir, naming())
        } while (!overwrite && session.exists(filePath))
        return filePath
    }

    private fun locateSimple(uploadDir: FileDirectory, session: FileStoreSession): FilePath {
        val filePath = FilePath(uploadDir, filename!!)
        if (!overwrite && session.exists(filePath)) {
            throw IllegalStateException("could not save $filePath")
        } else {
            return filePath
        }
    }
}
