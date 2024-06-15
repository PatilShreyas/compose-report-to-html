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

import okio.FileNotFoundException
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath

/**
 * Checks whether directory with [path] exists or not.
 * Else throws [FileNotFoundException].
 */
inline fun ensureDirectory(
    path: String,
    lazyMessage: () -> Any,
) {
    val absolutePath = FileSystem.SYSTEM.canonicalize(path.toPath())
    ensureDirectory(absolutePath, lazyMessage)
}

/**
 * Checks whether directory with [path] exists or not.
 * Else throws [FileNotFoundException].
 */
inline fun ensureDirectory(
    directory: Path,
    lazyMessage: () -> Any,
) {
    val fileSystem = FileSystem.SYSTEM
    val absolutePath = if (directory.isAbsolute) directory else fileSystem.canonicalize(directory)
    println("Checking directory '$absolutePath'")
    val isDirectory = fileSystem.metadataOrNull(directory)?.isDirectory == true
    if (!isDirectory) {
        val message = lazyMessage()
        throw FileNotFoundException(message.toString())
    }
}

/**
 * Checks whether file with [filename] exists or not. Else throws [FileNotFoundException].
 */
inline fun ensureFileExists(
    filename: String,
    lazyMessage: () -> Any,
): Path {
    val absolutePath = FileSystem.SYSTEM.canonicalize(filename.toPath())
    return ensureFileExists(absolutePath, lazyMessage)
}

/**
 * Checks whether [file] with exists or not. Else throws [FileNotFoundException].
 */
inline fun ensureFileExists(
    file: Path,
    lazyMessage: () -> Any,
): Path {
    val fileSystem = FileSystem.SYSTEM
    val absolutePath = if (file.isAbsolute) file else fileSystem.canonicalize(file)
    println("Checking file '$absolutePath'")
    if (!fileSystem.exists(file)) {
        val message = lazyMessage()
        throw FileNotFoundException(message.toString())
    }
    return file
}
