package com.jacknie.fd

import com.jacknie.fd.fs.FsFileDeliveryFactory
import com.jacknie.fd.sftp.Sftp
import com.jacknie.fd.sftp.SftpFileDeliveryFactory
import com.jcraft.jsch.ChannelSftp
import com.jcraft.jsch.SftpException
import io.mockk.every
import io.mockk.mockk
import org.slf4j.LoggerFactory
import java.io.ByteArrayInputStream
import java.io.File
import java.io.IOException
import java.nio.file.FileSystems
import java.nio.file.NoSuchFileException
import kotlin.test.Test
import kotlin.test.assertEquals

class FileDeliveryTest {

    private val logger = LoggerFactory.getLogger(javaClass)

    @Test(expected = NoSuchFileException::class) fun fsIntTest() {
        val policy = mockk<FilePolicy>()
        val userHome = System.getProperty("user.home")
        every { policy.getSystemPath(any()) } answers { getTodayIndexPath(userHome, this.arg(0), File.separator) }
        every { policy.generateFilename(any()) } answers { getUUIDFilename(this.arg(0)) }
        every { policy.verifyFileSource(any()) } answers { FileVerification(this.arg(0), emptyList()) }
        val factory = FsFileDeliveryFactory(FileSystems.getDefault())
        val fileDelivery = factory.createFileDelivery()
        val text = "Hello, World!!"
        val content = ByteArrayInputStream(text.toByteArray())
        val source = FileSource("test/path", content, text.length.toLong(), "test.txt", "txt", "text/plain")
        val file = fileDelivery.put(policy, source)
        logger.debug("{}", file)
        val inputStream = fileDelivery.get(file)
        assertEquals(text, inputStream.bufferedReader().use { it.readText() })
        fileDelivery.delete(file)
        fileDelivery.get(file)
    }

    /*@Test*/ fun sftpIntTest() {
        val policy = mockk<FilePolicy>()
        every { policy.getSystemPath(any()) } answers { getTodayIndexPath("/home/jacknie", this.arg(0)) }
        every { policy.generateFilename(any()) } answers { getUUIDFilename(this.arg(0)) }
        every { policy.verifyFileSource(any()) } answers { FileVerification(this.arg(0), emptyList()) }
        val sftp = Sftp(
                host = "host",
                user = "user",
                password = "password"
        )
        val factory = SftpFileDeliveryFactory(sftp)
        val fileDelivery = factory.createFileDelivery()
        val text = "Hello, World!!"
        val content = ByteArrayInputStream(text.toByteArray())
        val source = FileSource("test/path", content, text.length.toLong(), "test.txt", "txt", "text/plain")
        val file = fileDelivery.put(policy, source)
        logger.debug("{}", file)
        val inputStream = fileDelivery.get(file)
        assertEquals(text, inputStream.bufferedReader().use { it.readText() })
        fileDelivery.delete(file)
        try {
            fileDelivery.get(file)
        } catch (e: IOException) {
            val error = e.cause as SftpException
            assertEquals(ChannelSftp.SSH_FX_NO_SUCH_FILE, error.id)
        }
    }
}