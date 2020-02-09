package com.jacknie.fd.fs

import com.jacknie.fd.FileDelivery
import com.jacknie.fd.FileDeliveryFactory
import java.nio.file.FileSystem

class FsFileDeliveryFactory(private val fs: FileSystem): FileDeliveryFactory<FsFileStoreSession> {

    override fun createFileDelivery(): FileDelivery<FsFileStoreSession> {
        val fileStore = FsFileStore()
        val session = FsFileStoreSession(fs)
        return FileDelivery(fileStore, session)
    }
}