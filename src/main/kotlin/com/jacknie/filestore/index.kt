package com.jacknie.filestore

import com.jacknie.filestore.model.FilePath
import com.jacknie.filestore.model.UploadPolicy
import com.jacknie.filestore.model.UploadSource
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.nio.file.StandardOpenOption
import java.util.*

typealias FileStoreLocator = (FileStoreSession) -> FilePath
typealias UploadValidator = (UploadSource, UploadPolicy) -> Unit

/**
 * FileStoreSession 에서 제공 되는 InputStream 들을 임시 파일로 저장 해서 최종 사용 자는 io 예외가 발생 하지 않게 처리
 */
fun ensureFileContent(content: InputStream): InputStream {
    val prefix = UUID.randomUUID().toString()
    val suffix = ".tmp"
    val tempFilePath = Files.createTempFile(prefix, suffix)
    Files.copy(content, tempFilePath, StandardCopyOption.REPLACE_EXISTING)
    return Files.newInputStream(tempFilePath, StandardOpenOption.DELETE_ON_CLOSE)
}