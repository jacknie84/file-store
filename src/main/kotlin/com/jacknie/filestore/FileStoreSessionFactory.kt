package com.jacknie.filestore

import java.io.IOException

/**
 * 파일 저장소 세션 객체 생성자
 */
interface FileStoreSessionFactory<S: FileStoreSession> {

    /**
     * 파일 저장소 세션 객체 생성
     */
    @Throws(IOException::class)
    fun create(): S
}