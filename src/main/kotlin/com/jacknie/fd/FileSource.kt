package com.jacknie.fd

import java.io.InputStream
import javax.activation.MimeType

data class FileSource(

        /**
         * 파일 저장 경로
         */
        var storePath: String,

        /**
         * 파일 내용을 리턴한다.
         */
        var content: InputStream,

        /**
         * bytes 파일 사이즈를 리턴한다.
         */
        var fileSize: Long,

        /**
         * 파일 확장자를 포함한 파일 이름을 리턴한다.
         */
        var filename: String,

        /**
         * 파일 확장자를 리턴한다.
         */
        var extension: String,

        /**
         * 파일 마임 타입을 리턴한다.
         */
        var mimeType: String = "application/octet-stream",

        /**
         * 덮어 씌우기 허용 여부
         */
        var overwrite: Boolean = false
)