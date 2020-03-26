package com.jacknie.fd

data class DeliveredFile(

        /**
         * 저장 된 파일의 경로를 리턴한다.
         */
        var path: String,

        /**
         * 저장 된 파일의 확장자를 포함한 이름을 리턴한다.
         */
        var filename: String,

        /**
         * 저장 된 파일의 확장자를 리턴한다.
         */
        var extension: String?,

        /**
         * 저장 된 파일의 마임타입을 리턴한다.
         */
        var mimeType: String?,

        /**
         * 저장 된 bytes 파일 사이즈를 리턴한다.
         */
        var filesize: Long,

        /**
         * 저장 된 파일의 원래 이름을 리턴한다.
         */
        var originalFilename: String
)