package com.jacknie.filestore

import com.jacknie.filestore.model.FilePath
import com.jacknie.filestore.model.UploadPolicy
import com.jacknie.filestore.model.UploadSource
import java.io.IOException
import java.io.InputStream

/**
 * 파일 저장소 기능 추상화.
 */
interface FileStoreSession: AutoCloseable {

    /**
     * 파일 존재 여부 확인
     */
    @Throws(IOException::class)
    fun exists(filePath: FilePath): Boolean

    /**
     * 파일 저장 처리
     */
    @Throws(IOException::class)
    fun save(filePath: FilePath, content: InputStream)

    /**
     * 파일 로드
     */
    @Throws(IOException::class)
    fun load(filePath: FilePath): InputStream {
        val content = getFileContent(filePath)
        return ensureFileContent(content)
    }

    /**
     * 파일 내용 조회
     */
    @Throws(IOException::class)
    fun getFileContent(filePath: FilePath): InputStream
    
    /**
     * 파일 삭제 처리
     */
    @Throws(IOException::class)
    fun delete(filePath: FilePath)
}