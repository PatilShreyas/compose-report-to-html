/**
 * MIT License
 *
 * Copyright (c) 2022 Shreyas Patil
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
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
