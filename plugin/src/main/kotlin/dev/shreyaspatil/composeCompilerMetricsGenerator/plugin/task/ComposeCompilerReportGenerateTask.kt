package dev.shreyaspatil.composeCompilerMetricsGenerator.plugin.task

import com.android.build.api.variant.Variant
import dev.shreyaspatil.composeCompilerMetricsGenerator.core.ComposeCompilerMetricsProvider
import dev.shreyaspatil.composeCompilerMetricsGenerator.core.ComposeCompilerRawReportProvider
import dev.shreyaspatil.composeCompilerMetricsGenerator.core.utils.ensureDirectory
import dev.shreyaspatil.composeCompilerMetricsGenerator.generator.HtmlReportGenerator
import dev.shreyaspatil.composeCompilerMetricsGenerator.generator.ReportSpec
import dev.shreyaspatil.composeCompilerMetricsGenerator.plugin.ComposeCompilerReportExtension
import java.io.File
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.GradleBuild
import org.gradle.configurationcache.extensions.capitalized

const val KEY_ENABLE_REPORT_GEN = "dev.shreyaspatil.composeCompiler.reportGen.enable"

fun Project.configureComposeCompilerReportGenTaskForVariant(
    variant: Variant,
    reportExtension: ComposeCompilerReportExtension
) {
    tasks.register(
        variant.name + "ComposeCompilerHtmlReport",
        GradleBuild::class.java
    ) {
        startParameter.projectProperties[KEY_ENABLE_REPORT_GEN] = true.toString()
        tasks = listOf("compile${variant.name.capitalized()}Kotlin")

        doFirst {
            cleanupDirectory(reportExtension.outputDirectory.get())
        }

        doLast {
            generateReport(reportExtension)
        }
    }
}

fun cleanupDirectory(directory: File) {
    ensureDirectory(directory) { "'${directory}' is not a directory" }
    directory.deleteRecursively()
}

/**
 * Returns true if currently executing task is about generating compose compiler report
 */
fun Project.executingComposeCompilerReportGenerationGradleTask() = runCatching {
    property(KEY_ENABLE_REPORT_GEN)
}.getOrNull() == "true"

private fun Task.generateReport(reportExtension: ComposeCompilerReportExtension) {
    val outputDirectory = reportExtension.outputDirectory.get()

    // Create a report specification with application name
    val reportSpec = ReportSpec(reportExtension.name.get())

    val rawReportProvider = ComposeCompilerRawReportProvider.FromDirectory(outputDirectory)

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