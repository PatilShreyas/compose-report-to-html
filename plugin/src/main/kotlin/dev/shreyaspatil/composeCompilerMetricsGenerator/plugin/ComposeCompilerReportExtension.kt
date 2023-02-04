package dev.shreyaspatil.composeCompilerMetricsGenerator.plugin

import org.gradle.api.provider.Property
import java.io.File
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create

interface ComposeCompilerReportExtension {
    /**
     * Enable report generation
     */
    val enable: Property<Boolean>

    /**
     * Enable calculating metrics from the compose compiler in the main report.
     */
    val enableMetrics: Property<Boolean>

    /**
     * Enable extracting statistical report from the compose compiler in the main report.
     */
    val enableReport: Property<Boolean>

    /**
     * Name of the report. It can be a name of application, module, etc.
     */
    val name: Property<String>

    /**
     * The directory where report will be stored.
     */
    val outputDirectory: Property<File>

    companion object {
        const val NAME = "htmlComposeCompilerReport"

        fun get(target: Project) = target.extensions.create<ComposeCompilerReportExtension>(NAME).apply {
            enable.convention(true)
            enableReport.convention(true)
            enableMetrics.convention(true)
            name.convention(target.rootProject.name)
            outputDirectory.convention(target.rootProject.buildDir.resolve("/compose_metrics"))
        }
    }
}