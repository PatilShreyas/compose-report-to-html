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
package dev.shreyaspatil.composeCompilerMetricsGenerator.plugin.task

import com.android.build.api.variant.Variant
import dev.shreyaspatil.composeCompilerMetricsGenerator.core.ComposeCompilerMetricsProvider
import dev.shreyaspatil.composeCompilerMetricsGenerator.core.ComposeCompilerRawReportProvider
import dev.shreyaspatil.composeCompilerMetricsGenerator.generator.HtmlReportGenerator
import dev.shreyaspatil.composeCompilerMetricsGenerator.generator.ReportSpec
import dev.shreyaspatil.composeCompilerMetricsGenerator.plugin.ComposeCompilerReportExtension
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.GradleBuild
import org.gradle.configurationcache.extensions.capitalized
import java.io.File
import java.io.FileNotFoundException

const val KEY_ENABLE_REPORT_GEN = "dev.shreyaspatil.composeCompiler.reportGen.enable"

fun Project.createComposeCompilerReportGenTaskForVariant(
    variant: Variant,
    reportExtension: ComposeCompilerReportExtension
) {
    val taskName = variant.name + "ComposeCompilerHtmlReport"
    tasks.register(taskName, GradleBuild::class.java) {
        startParameter.projectProperties[KEY_ENABLE_REPORT_GEN] = true.toString()
        tasks = listOf("compile${variant.name.capitalized()}Kotlin")

        doFirst {
            cleanupDirectory(reportExtension.outputPath.get())
        }

        doLast {
            generateReport(reportExtension)
        }

        group = "compose compiler report"
        description = "Generate Compose Compiler Metrics and Report"
    }
}

fun cleanupDirectory(directory: String) {
    val dirFile = File(directory)
    if (dirFile.exists()) {
        if (!dirFile.isDirectory) {
            throw FileNotFoundException("'$directory' is not a directory")
        }
    }

    dirFile.deleteRecursively()
}

/**
 * Returns true if currently executing task is about generating compose compiler report
 */
fun Project.executingComposeCompilerReportGenerationGradleTask() = runCatching {
    property(KEY_ENABLE_REPORT_GEN)
}.getOrNull() == "true"

private fun Task.generateReport(reportExtension: ComposeCompilerReportExtension) {
    val outputDirectory = reportExtension.outputPath.get().let { File(it) }

    // Create a report specification with application name
    val reportSpec = ReportSpec(reportExtension.name.get())

    val rawReportProvider = ComposeCompilerRawReportProvider.FromDirectory(
        directory = reportExtension.composeRawMetricsOutputDirectory
    )

    // Provide metric files to generator
    val htmlGenerator = HtmlReportGenerator(
        reportSpec = reportSpec,
        metricsProvider = ComposeCompilerMetricsProvider(rawReportProvider)
    )

    // Generate HTML (String)
    val html = htmlGenerator.generateHtml()

    // Create a report file
    val file = outputDirectory.resolve("index.html")
    file.writeText(html)

    logger.quiet("Compose Compiler report is generated: ${file.absolutePath}")
}
