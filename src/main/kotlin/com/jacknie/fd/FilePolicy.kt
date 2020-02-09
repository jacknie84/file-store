package com.jacknie.fd

interface FilePolicy {

    fun getSystemPath(source: FileSource): String

    fun generateFilename(source: FileSource): String

    fun verifyFileSource(source: FileSource): FileVerification

}