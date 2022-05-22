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
package dev.shreyaspatil.composeCompilerMetricsGenerator.cli

import dev.shreyaspatil.composeCompilerMetricsGenerator.core.ComposeCompilerMetricsProvider
import dev.shreyaspatil.composeCompilerMetricsGenerator.generator.HtmlReportGenerator
import dev.shreyaspatil.composeCompilerMetricsGenerator.generator.ReportSpec
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.required
import java.io.File
import java.io.FileNotFoundException
import java.nio.file.Paths

/**
 * Entry point of a CLI application
 */
fun main(args: Array<String>) {
    val arguments = CliArguments(args)

    val reportSpec = ReportSpec(arguments.applicationName)

    printHeader("Generating Composable HTML Report")

    val html = HtmlReportGenerator(
        reportSpec = reportSpec,
        metricsProvider = ComposeCompilerMetricsProvider(
            briefStatisticsJsonFilePath = arguments.overallStatsFile,
            detailedStatisticsJsonFilePath = arguments.detailedStatsFile,
            composableReportFilePath = arguments.composableMetricsFile,
            classesReportFilePath = arguments.classMetricsFile
        )
    ).generateHtml()

    printHeader("Saving Composable Report")
    val reportPath = saveReportAsHtml(html, arguments.outputDirectory)

    println("Report for '${reportSpec.name}' is successfully generated at '$reportPath'")
    println("DONE!")
}

/**
 * Saves a file with [htmlContent] having name as 'index.html' at specified [outputDirectory]
 */
fun saveReportAsHtml(htmlContent: String, outputDirectory: String): String {
    val directory = File(Paths.get(outputDirectory).toAbsolutePath().toString())

    if (!directory.exists()) {
        if (!directory.mkdirs()) {
            // When it fails to create a directory, create a temporary file so that developers can atleast access it.
            val file = File.createTempFile("index", ".html")
            file.writeText(htmlContent)
            error("Failed to create directory '$outputDirectory'. Have created temporary file at '${file.canonicalPath}'")
        }
    }
    val file = File("${directory.absolutePath}/index.html")
    file.writeText(htmlContent)

    return file.canonicalPath
}

/**
 * Parses and validates CLI arguments
 */
class CliArguments(args: Array<String>) {
    private val parser = ArgParser("Compose Compiler Report to HTML Generator ~ ${Constants.VERSION}")

    val applicationName by parser.option(
        ArgType.String,
        shortName = "app",
        description = "Application name (To be displayed in the report)"
    ).required()

    val overallStatsFile by parser.option(
        ArgType.String,
        shortName = "overallStatsReport",
        description = "Overall Statistics Metrics JSON file"
    ).required()

    val detailedStatsFile by parser.option(
        ArgType.String,
        shortName = "detailedStatsMetrics",
        description = "Detailed Statistics Metrics CSV file"
    ).required()

    val composableMetricsFile by parser.option(
        ArgType.String,
        shortName = "composableMetrics",
        description = "Composable Metrics TXT file"
    ).required()

    val classMetricsFile by parser.option(
        ArgType.String,
        shortName = "classMetrics",
        description = "Class Metrics TXT file"
    ).required()

    val outputDirectory by parser.option(
        ArgType.String,
        shortName = "o",
        description = "Output directory name"
    ).required()

    init {
        parser.parse(args)

        printHeader("Validating Arguments")

        require(applicationName.isNotBlank()) { "Application name should not be blank" }
        require(outputDirectory.isNotBlank()) { "Output directory path should not be blank" }

        printHeader("Checking files")
        listOf(overallStatsFile, detailedStatsFile, composableMetricsFile, classMetricsFile).forEach { filename ->
            ensureFileExists(filename) { fileNotExistMessage(filename) }
        }
    }

    companion object {
        fun fileNotExistMessage(filename: String) = "File not exists with name '$filename'"
    }
}

/**
 * Checks whether file with [filename] exists or not. Else throws [FileNotFoundException].
 */
inline fun ensureFileExists(filename: String, lazyMessage: () -> Any) {
    val file = File(Paths.get(filename).toAbsolutePath().toString())
    println("Checking file '${file.absolutePath}'")
    if (!file.exists()) {
        val message = lazyMessage()
        throw FileNotFoundException(message.toString())
    }
}

fun printHeader(header: String) = println(
    """
    ------------------------------------------------------------------
    $header
    """.trimIndent()
)

object Constants {
    const val VERSION = "v1.0.0-alpha01-dev03"
}