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
import dev.shreyaspatil.composeCompilerMetricsGenerator.generator.ReportOptions
import dev.shreyaspatil.composeCompilerMetricsGenerator.generator.ReportSpec
import dev.shreyaspatil.composeCompilerMetricsGenerator.plugin.ComposeCompilerReportExtension
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskProvider
import org.gradle.tooling.GradleConnector
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget
import java.io.File
import java.io.FileNotFoundException

const val KEY_ENABLE_REPORT_GEN = "dev.shreyaspatil.composeCompiler.reportGen.enable"

@CacheableTask
abstract class ComposeCompilerReportGenerateTask : DefaultTask() {
    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val projectDirectory: DirectoryProperty

    @get:Input
    abstract val compileKotlinTasks: Property<String>

    @get:Input
    abstract val reportName: Property<String>

    @get:OutputDirectory
    abstract val composeRawMetricsOutputDirectory: DirectoryProperty

    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty

    @get:Input
    abstract val includeStableComposables: Property<Boolean>

    @get:Input
    abstract val includeStableClasses: Property<Boolean>

    @get:Input
    abstract val includeClasses: Property<Boolean>

    @get:Input
    abstract val showOnlyUnstableComposables: Property<Boolean>

    @TaskAction
    fun generate() {
        val outputDirectory = outputDirectory.get().asFile
        cleanupDirectory(outputDirectory)

        generateRawMetricsAndReport()

        generateReport(outputDirectory)
    }

    private fun generateRawMetricsAndReport() {
        GradleConnector.newConnector().forProjectDirectory(projectDirectory.get().asFile)
            .connect()
            .use {
                it.newBuild()
                    .setStandardOutput(System.out)
                    .setStandardError(System.err)
                    .setStandardInput(System.`in`)
                    .forTasks(compileKotlinTasks.get())
                    .withArguments(
                        // Re-running is necessary. In case devs deleted raw files and if task uses cache
                        // then this task will explode 💣
                        "--rerun-tasks",
                        // Signal for enabling report generation in `kotlinOptions{}` block.
                        "-P$KEY_ENABLE_REPORT_GEN=true",
                    )
                    .run()
            }
    }

    private fun generateReport(outputDirectory: File) {
        // Create a report specification with application name
        val reportSpec =
            ReportSpec(
                name = reportName.get(),
                options =
                    ReportOptions(
                        includeStableComposables = includeStableComposables.get(),
                        includeStableClasses = includeStableClasses.get(),
                        includeClasses = includeClasses.get(),
                        showOnlyUnstableComposables = showOnlyUnstableComposables.get(),
                    ),
            )

        val rawReportProvider =
            ComposeCompilerRawReportProvider.FromDirectory(
                directory = composeRawMetricsOutputDirectory.get().asFile,
            )

        // Provide metric files to generator
        val htmlGenerator =
            HtmlReportGenerator(
                reportSpec = reportSpec,
                metricsProvider = ComposeCompilerMetricsProvider(rawReportProvider),
            )

        // Generate HTML (as String)
        val html = htmlGenerator.generateHtml()

        // Create a report file
        val file = outputDirectory.resolve("index.html")
        file.writeText(html)

        val reportUrl = file.toURI().toURL().toExternalForm()
        logger.quiet("Compose Compiler report is generated: $reportUrl")
    }

    private fun cleanupDirectory(outputDirectory: File) {
        if (outputDirectory.exists()) {
            if (!outputDirectory.isDirectory) {
                throw FileNotFoundException("'$outputDirectory' is not a directory")
            }
        }

        outputDirectory.deleteRecursively()
    }
}

fun Project.registerComposeCompilerReportGenTaskForTarget(
    target: KotlinTarget,
    buildType: String? = null,
): TaskProvider<ComposeCompilerReportGenerateTask> {
    val taskName = target.name + (buildType ?: "") + "ComposeCompilerHtmlReport"
    val compileKotlinTaskName = compileKotlinTaskNameFromTarget(target, buildType)
    val descSuffix =
        buildString {
            append("'${target.name}' target")
            if (buildType != null) {
                append(" '$buildType' variant")
            }
            append(" in multiplatform project")
        }
    return registerComposeCompilerReportGenTask(taskName, compileKotlinTaskName, descSuffix)
}

fun Project.registerComposeCompilerReportGenTaskForJvmProject(projectName: String): TaskProvider<ComposeCompilerReportGenerateTask> {
    val taskName = projectName + "ComposeCompilerHtmlReport"
    val compileKotlinTaskName = "compileKotlin"
    val descSuffix = "'Jvm/Desktop' Project"
    return registerComposeCompilerReportGenTask(taskName, compileKotlinTaskName, descSuffix)
}

fun Project.registerComposeCompilerReportGenTaskForVariant(variant: Variant): TaskProvider<ComposeCompilerReportGenerateTask> {
    val taskName = variant.name + "ComposeCompilerHtmlReport"
    val compileKotlinTaskName = compileKotlinTaskNameFromVariant(variant)
    val descSuffix = "'${variant.name}' variant in android project"
    return registerComposeCompilerReportGenTask(taskName, compileKotlinTaskName, descSuffix)
}

fun Project.registerComposeCompilerReportGenTask(
    taskName: String,
    compileKotlinTaskName: String,
    descSuffix: String,
): TaskProvider<ComposeCompilerReportGenerateTask> {
    val reportExtension = ComposeCompilerReportExtension.get(project)

    return tasks.register(taskName, ComposeCompilerReportGenerateTask::class.java) {
        projectDirectory.set(layout.projectDirectory)
        compileKotlinTasks.set(compileKotlinTaskName)
        reportName.set(reportExtension.name)
        composeRawMetricsOutputDirectory.set(reportExtension.composeRawMetricsOutputDirectory)
        outputDirectory.set(layout.dir(reportExtension.outputDirectory))
        includeStableComposables.set(reportExtension.includeStableComposables)
        includeStableClasses.set(reportExtension.includeStableClasses)
        includeClasses.set(reportExtension.includeClasses)
        showOnlyUnstableComposables.set(reportExtension.showOnlyUnstableComposables)

        group = "compose compiler report"
        description = "Generate Compose Compiler Metrics and Report for $descSuffix"
    }
}

/**
 * Returns true if currently executing task is about generating compose compiler report
 */
fun Project.executingComposeCompilerReportGenerationGradleTask() =
    runCatching {
        property(KEY_ENABLE_REPORT_GEN)
    }.getOrNull() == "true"

/**
 * Returns a task name for compile<VARIANT>Kotlin with [variant]
 */
fun compileKotlinTaskNameFromVariant(variant: Variant): String {
    val variantName = variant.name.let { it[0].toUpperCase() + it.substring(1) }
    return "compile${variantName}Kotlin"
}

/**
 * Returns a task name for compile<BUILD_TYPE>Kotlin<TARGET> with [target]
 */
fun compileKotlinTaskNameFromTarget(
    target: KotlinTarget,
    buildType: String?,
): String {
    val targetName = target.name.let { it[0].toUpperCase() + it.substring(1) }
    val buildTypeName = buildType?.let { it[0].toUpperCase() + it.substring(1) } ?: ""
    return "compile${buildTypeName}Kotlin$targetName"
}
