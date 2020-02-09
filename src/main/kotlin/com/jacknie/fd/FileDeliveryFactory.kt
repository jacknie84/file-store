package com.jacknie.fd

interface FileDeliveryFactory<T : FileStoreSession> {

    /**
     * 파일 전송 객체를 생성한다.
     */
    fun createFileDelivery(): FileDelivery<T>
}