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
import dev.shreyaspatil.composeCompilerMetricsGenerator.core.ComposeCompilerRawReportProvider
import dev.shreyaspatil.composeCompilerMetricsGenerator.core.utils.ensureDirectory
import dev.shreyaspatil.composeCompilerMetricsGenerator.core.utils.ensureFileExists
import dev.shreyaspatil.composeCompilerMetricsGenerator.generator.HtmlReportGenerator
import dev.shreyaspatil.composeCompilerMetricsGenerator.generator.ReportSpec
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import kotlinx.cli.required
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths

/**
 * Entry point of a CLI application
 */
fun main(args: Array<String>) {
    val arguments = CliArguments(args, Paths.get("").toAbsolutePath())

    val reportSpec = ReportSpec(arguments.applicationName)
    val rawReportProvider = arguments.getRawReportProvider()

    printHeader("Generating Composable HTML Report")

    val html = HtmlReportGenerator(
        reportSpec = reportSpec,
        metricsProvider = ComposeCompilerMetricsProvider(rawReportProvider)
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
class CliArguments(args: Array<String>, private val path: Path) {
    private val parser = ArgParser("Compose Compiler Report to HTML Generator ~ ${Constants.VERSION}")

    val applicationName by parser.option(
        ArgType.String,
        shortName = "app",
        description = "Application name (To be displayed in the report)"
    ).required()

    val inputDirectory by parser.option(
        ArgType.String,
        shortName = "i",
        description = "Input directory where composable report and metrics are available"
    )

    val overallStatsFile by parser.option(
        ArgType.String,
        shortName = "overallStatsReport",
        description = "Overall Statistics Metrics JSON files (separated by commas)"
    )

    val detailedStatsFile by parser.option(
        ArgType.String,
        shortName = "detailedStatsMetrics",
        description = "Detailed Statistics Metrics CSV files (separated by commas)"
    )

    val composableMetricsFile by parser.option(
        ArgType.String,
        shortName = "composableMetrics",
        description = "Composable Metrics TXT files (separated by commas)"
    )

    val classMetricsFile by parser.option(
        ArgType.String,
        shortName = "classMetrics",
        description = "Class Metrics TXT files (separated by commas)"
    )

    val outputDirectory by parser.option(
        ArgType.String,
        shortName = "o",
        description = "Output directory name"
    ).default(path.toAbsolutePath().toString())

    init {
        parser.parse(args)

        printHeader("Validating Arguments")

        require(applicationName.isNotBlank()) { "Application name should not be blank" }
        require(outputDirectory.isNotBlank()) { "Output directory path should not be blank" }
    }

    fun getRawReportProvider(): ComposeCompilerRawReportProvider {
        val directory = inputDirectory

        val files = arrayOf(
            overallStatsFile,
            detailedStatsFile,
            composableMetricsFile,
            classMetricsFile
        )

        return if (directory != null) {
            ensureDirectory(directory) { "Directory '$directory' not exists" }
            getRawReportProviderFromDirectory(directory)
        } else if (files.all { !it.isNullOrBlank() }) {
            getRawReportProviderFromIndividualFiles()
        } else {
            // Assume report and metric files available in the current working directory as specified in the `path`
            val defaultDirectory = path.toAbsolutePath().toString()
            getRawReportProviderFromDirectory(defaultDirectory)
        }
    }

    private fun getRawReportProviderFromIndividualFiles(): ComposeCompilerRawReportProvider {
        return ComposeCompilerRawReportProvider.FromIndividualFiles(
            briefStatisticsJsonFiles = files(overallStatsFile!!),
            detailedStatisticsCsvFiles = files(detailedStatsFile!!),
            composableReportFiles = files(composableMetricsFile!!),
            classesReportFiles = files(classMetricsFile!!)
        )
    }

    private fun getRawReportProviderFromDirectory(directory: String): ComposeCompilerRawReportProvider.FromDirectory {
        return ComposeCompilerRawReportProvider.FromDirectory(File(Paths.get(directory).toAbsolutePath().toString()))
    }

    private fun files(filenames: String): List<File> {
        return filenames.split(",").map { ensureFileExists(it) { "File not exist: $it" } }
    }
}

fun printHeader(header: String) = println(
    """
    ------------------------------------------------------------------
    $header
    """.trimIndent()
)

object Constants {
    const val VERSION = "v1.0.0-beta01"
}
