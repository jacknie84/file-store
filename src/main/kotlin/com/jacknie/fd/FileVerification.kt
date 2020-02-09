package com.jacknie.fd

import com.jacknie.fd.FileSource

data class FileVerification (

        /**
         * 검증 대상 파일
         */
        var source: FileSource,

        /**
         * 파일 검증 결과 거부 결과 목록
         */
        var rejects: List<Map<String, Any>>
)