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
package dev.shreyaspatil.composeCompilerMetricsGenerator.core

import dev.shreyaspatil.composeCompilerMetricsGenerator.core.file.ReportAndMetricsFileFinder
import dev.shreyaspatil.composeCompilerMetricsGenerator.core.utils.ensureDirectory
import dev.shreyaspatil.composeCompilerMetricsGenerator.core.utils.ensureFileExists
import okio.Path

/**
 * Provide files of compose compiler metrics and reports
 */
sealed interface ComposeCompilerRawReportProvider {
    val briefStatisticsJsonFiles: List<Path>
    val detailedStatisticsCsvFiles: List<Path>
    val composableReportFiles: List<Path>
    val classesReportFiles: List<Path>

    /**
     * Provides report from individual files
     */
    class FromIndividualFiles(
        override val briefStatisticsJsonFiles: List<Path>,
        override val detailedStatisticsCsvFiles: List<Path>,
        override val composableReportFiles: List<Path>,
        override val classesReportFiles: List<Path>,
    ) : ComposeCompilerRawReportProvider {
        init {
            validateComposeCompilerRawReportProvider()
        }
    }

    /**
     * Searches for files in the given [directory] and provides report and metric files found in that directory.
     */
    class FromDirectory(directory: Path) : ComposeCompilerRawReportProvider {
        private val finder = ReportAndMetricsFileFinder(directory)

        override val briefStatisticsJsonFiles: List<Path> = finder.findBriefStatisticsJsonFile()
        override val detailedStatisticsCsvFiles: List<Path> = finder.findDetailsStatisticsCsvFile()
        override val composableReportFiles: List<Path> = finder.findComposablesReportTxtFile()
        override val classesReportFiles: List<Path> = finder.findClassesReportTxtFile()

        init {
            ensureDirectory(directory) { "Directory '$directory' not exists" }
            validateComposeCompilerRawReportProvider()
        }
    }
}

/**
 * Validates report and metric files
 */
fun ComposeCompilerRawReportProvider.validateComposeCompilerRawReportProvider() {
    val files =
        briefStatisticsJsonFiles +
            detailedStatisticsCsvFiles +
            composableReportFiles +
            classesReportFiles

    files.forEach { ensureFileExists(it) { "File '$it' not exists" } }
}
