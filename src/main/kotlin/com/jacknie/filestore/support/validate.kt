package com.jacknie.filestore.support

import com.jacknie.filestore.UploadPolicyException
import com.jacknie.filestore.model.Filename
import com.jacknie.filestore.model.UploadPolicy
import com.jacknie.filestore.model.UploadSource

@Throws(UploadPolicyException::class)
fun validateUploadSource(source: UploadSource, policy: UploadPolicy) {
    var valid = isValidFilename(source.filename, policy) &&
            isValidFilesize(source.filesize, policy) &&
            isValidMimeType(source.mimType, policy)
    if (!valid) {
        throw UploadPolicyException(source, policy)
    }
}

fun isValidFilename(filename: Filename, policy: UploadPolicy): Boolean {
    if (policy.allowedFilenameExts.isNullOrEmpty()) {
        return false
    }
    if (filename.extension.isNullOrBlank()) {
        return true
    }
    val allowedFilenameExts = if (policy.filenameExtCaseSensitive) {
        policy.allowedFilenameExts
    } else {
        policy.allowedFilenameExts.map { it.toLowerCase() }
    }
    val filenameExt = if (policy.filenameExtCaseSensitive) {
        filename.extension
    } else {
        filename.extension.toLowerCase()
    }
    return allowedFilenameExts.contains(filenameExt)
}

fun isValidFilesize(filesize: Long, policy: UploadPolicy) = policy.filesizeLimit >= filesize

fun isValidMimeType(mimeType: String, policy: UploadPolicy): Boolean {
    return if (policy.allowedMimeTypes.isNullOrEmpty()) {
        false
    } else {
        policy.allowedMimeTypes.any { it.matches(mimeType) }
    }
}