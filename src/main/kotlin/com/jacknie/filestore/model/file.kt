package com.jacknie.filestore.model

import java.io.File

data class FilePath(
        val directory: FileDirectory,
        var filename: Filename
) {
    override fun toString() = "$directory${directory.separator}$filename"
}

data class FileDirectory(
        var startsWithSeparator: Boolean,
        var separator: String,
        var pathNames: List<String>
) {
    constructor(directory: String): this("/", directory)

    constructor(separator: String, directory: String): this (
            startsWithSeparator = directory.startsWith(separator),
            separator = separator,
            pathNames = directory.split(separator).filter { !it.isNullOrBlank() }
    )

    override fun toString(): String {
        val path = pathNames.joinToString(separator)
        return if (startsWithSeparator) "$separator$path" else path
    }

    fun resolve(other: String): FileDirectory {
        val directory = FileDirectory(separator, other)
        return resolve(directory)
    }

    fun resolve(other: FileDirectory): FileDirectory {
        val pathNames = arrayListOf(*pathNames.toTypedArray())
        pathNames.addAll(other.pathNames)
        return FileDirectory(startsWithSeparator, separator, pathNames)
    }

    fun resolve(filePath: FilePath): FilePath {
        val resolvedDir = resolve(filePath.directory)
        return FilePath(resolvedDir, filePath.filename)
    }

    fun forEach(action: (FileDirectory) -> Unit) {
        pathNames.forEachIndexed { i, pathName ->
            val directory = if (i == 0) {
                FileDirectory(startsWithSeparator, separator, listOf(pathName))
            } else {
                FileDirectory(false, separator, listOf(pathName))
            }
            action(directory)
        }
    }
}

data class Filename(
        var name: String,
        var extension: String
) {
    constructor(filename: String): this(File(filename))

    constructor(file: File): this(file.nameWithoutExtension, file.extension)

    override fun toString() = "$name.$extension"
}