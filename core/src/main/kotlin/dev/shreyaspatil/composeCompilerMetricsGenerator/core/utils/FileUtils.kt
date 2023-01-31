package dev.shreyaspatil.composeCompilerMetricsGenerator.core.utils

import java.io.File
import java.io.FileNotFoundException
import java.nio.file.Paths

/**
 * Checks whether directory with [path] exists or not.
 * Else throws [FileNotFoundException].
 */
inline fun ensureDirectory(path: String, lazyMessage: () -> Any) {
    val file = File(Paths.get(path).toAbsolutePath().toString())
    ensureDirectory(file, lazyMessage)
}

/**
 * Checks whether directory with [path] exists or not.
 * Else throws [FileNotFoundException].
 */
inline fun ensureDirectory(directory: File, lazyMessage: () -> Any) {
    println("Checking directory '${directory.absolutePath}'")
    if (!directory.isDirectory) {
        val message = lazyMessage()
        throw FileNotFoundException(message.toString())
    }
}

/**
 * Checks whether file with [filename] exists or not. Else throws [FileNotFoundException].
 */
inline fun ensureFileExists(filename: String, lazyMessage: () -> Any): File {
    val file = File(Paths.get(filename).toAbsolutePath().toString())
    return ensureFileExists(file, lazyMessage)
}

/**
 * Checks whether [file] with exists or not. Else throws [FileNotFoundException].
 */
inline fun ensureFileExists(file: File, lazyMessage: () -> Any): File {
    println("Checking file '${file.absolutePath}'")
    if (!file.exists()) {
        val message = lazyMessage()
        throw FileNotFoundException(message.toString())
    }
    return file
}