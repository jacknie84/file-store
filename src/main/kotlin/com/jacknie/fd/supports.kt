package com.jacknie.fd

import java.time.LocalDate
import java.util.*

/**
 * UUID 파일 이름 생성
 */
fun getUUIDFilename(source: FileSource): String {
    return "${UUID.randomUUID()}.${source.extension}"
}

/**
 * 시스템 오늘 일시 인덱스 경로를 가지는 파일 저장 경로를 생성
 */
fun getTodayIndexPath(pathPrefix: String, source: FileSource): String {
    val indexedPath = "$pathPrefix/${source.storePath}/${LocalDate.now()}"
    return indexedPath.replace("\\", "/")
}