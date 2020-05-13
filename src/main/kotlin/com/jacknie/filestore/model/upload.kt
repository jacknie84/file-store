package com.jacknie.filestore.model

import java.io.InputStream

data class UploadPolicy(
        var filenameExtCaseSensitive: Boolean,
        var allowedFilenameExts: Set<String>,
        var allowedMimeTypes: Set<Regex>,
        var filesizeLimit: Long
)

data class UploadSource(
        var filename: Filename,
        var mimType: String,
        var content: InputStream,
        var filesize: Long
)