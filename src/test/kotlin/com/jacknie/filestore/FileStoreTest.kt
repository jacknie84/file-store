package com.jacknie.filestore

import com.jacknie.filestore.filesystem.FileSystemStoreSessionFactory
import com.jacknie.filestore.model.FileDirectory
import com.jacknie.filestore.model.Filename
import com.jacknie.filestore.model.UploadPolicy
import com.jacknie.filestore.model.UploadSource
import com.jacknie.filestore.sftp.Sftp
import com.jacknie.filestore.sftp.SftpStoreSessionFactory
import com.jacknie.filestore.support.FileStoreLocatorBuilder
import com.jacknie.filestore.support.validateUploadSource
import com.jcraft.jsch.ChannelSftp
import com.jcraft.jsch.SftpException
import org.slf4j.LoggerFactory
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.nio.file.FileSystems
import java.nio.file.NoSuchFileException
import java.time.LocalDate
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class FileStoreTest {

    private val logger = LoggerFactory.getLogger(javaClass)
    private val policy = UploadPolicy(
            allowedFilenameExts = hashSetOf("txt"),
            allowedMimeTypes = hashSetOf("text/plain".toRegex()),
            filenameExtCaseSensitive = true,
            filesizeLimit = 1000
    )
    private val uploadDir = FileDirectory("test/path/${LocalDate.now()}")
    private val filename = Filename("test.txt")
    private val text = "Hello, World!!"
    private val source = UploadSource(
            content = ByteArrayInputStream(text.toByteArray()),
            filename = filename,
            filesize = text.length.toLong(),
            mimType = "text/plain"
    )
    private val uploadValidator = { s: UploadSource, p: UploadPolicy -> validateUploadSource(s, p) }
    private val locator = FileStoreLocatorBuilder()
            .uploadDir(uploadDir)
            .filename(filename)
            .naming { UUID.randomUUID().toString() }
            .build()

    @Test(expected = NoSuchFileException::class) fun `file system integration test`() {
        val userHomeDir = FileDirectory(File.separator, System.getProperty("user.home"))
        val storeFileDir = userHomeDir.resolve("file-store")
        val factory = FileSystemStoreSessionFactory(storeFileDir, FileSystems.getDefault())
        val fileStore = FileStoreTemplate(factory, uploadValidator)
        val filePath = fileStore.save(source, policy, locator)
        logger.debug("{}", filePath)

        val inputStream = fileStore.load(filePath)
        assertEquals(text, inputStream.bufferedReader().use { it.readText() })
        fileStore.delete(filePath)
        fileStore.load(filePath)
    }

    @Test(expected = FileNotFoundException::class) fun `sftp integration test`() {
        val sftp = Sftp(
                host = "insert your host",
                user = "insert your user",
                password = "insert yser password"
        )
        val userHome = "insert your user home directory"
        val userHomeDir = FileDirectory("/", userHome)
        val storeFileDir = userHomeDir.resolve("file-store")
        val factory = SftpStoreSessionFactory(storeFileDir, sftp)
        val fileStore = FileStoreTemplate(factory, uploadValidator)
        val filePath = fileStore.save(source, policy, locator)
        logger.debug("{}", filePath)

        val inputStream = fileStore.load(filePath)
        assertEquals(text, inputStream.bufferedReader().use { it.readText() })
        fileStore.delete(filePath)
        fileStore.load(filePath)
    }
}