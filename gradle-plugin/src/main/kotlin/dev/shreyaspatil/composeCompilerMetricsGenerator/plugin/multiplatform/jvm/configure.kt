package dev.shreyaspatil.composeCompilerMetricsGenerator.plugin.multiplatform.jvm

import dev.shreyaspatil.composeCompilerMetricsGenerator.plugin.multiplatform.configureKotlinOptionsForComposeCompilerReport
import dev.shreyaspatil.composeCompilerMetricsGenerator.plugin.task.executingComposeCompilerReportGenerationGradleTask
import dev.shreyaspatil.composeCompilerMetricsGenerator.plugin.task.registerComposeCompilerReportGenTaskForJvmProject
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

fun Project.configureKotlinJvmComposeCompilerReports(jvm: KotlinJvmProjectExtension) {
    // Create gradle tasks for generating report
    registerComposeCompilerReportGenTaskForJvmProject()

    afterEvaluate {
        // When this method returns true it means gradle task for generating report is executing otherwise
        // normal compilation task is executing.
        val isFromReportGenGradleTask = executingComposeCompilerReportGenerationGradleTask()
        if (isFromReportGenGradleTask) {
            jvm.target {
                configureKotlinOptionsForComposeCompilerReport(compilations)
            }
        }
    }
}