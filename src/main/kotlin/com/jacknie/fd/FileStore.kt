package com.jacknie.fd

import java.io.IOException
import java.io.InputStream

interface FileStore<T : FileStoreSession> {

    /**
     * 경로와 파일이름에 해당하는 파일이 있는지 여부
     */
    @Throws(IOException::class)
    fun exists(session: T, path: String, filename: String): Boolean

    /**
     * 경로와 파일이름에 파일을 저장
     */
    @Throws(IOException::class)
    fun save(session: T, path: String, filename: String, content: InputStream)

    /**
     * 경로와 파일이름의 파일을 로드
     */
    @Throws(IOException::class)
    fun load(session: T, path: String, filename: String): InputStream

    /**
     * 경로와 파일이름의 파일을 삭제
     */
    @Throws(IOException::class)
    fun delete(session: T, path: String, filename: String)
}