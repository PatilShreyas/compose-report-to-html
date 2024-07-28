package dev.shreyaspatil.composeCompilerMetricsGenerator.plugin.multiplatform.android

import com.android.build.api.dsl.CommonExtension
import com.android.build.api.variant.AndroidComponentsExtension
import dev.shreyaspatil.composeCompilerMetricsGenerator.plugin.multiplatform.configureKotlinOptionsForComposeCompilerReport
import dev.shreyaspatil.composeCompilerMetricsGenerator.plugin.task.executingComposeCompilerReportGenerationGradleTask
import dev.shreyaspatil.composeCompilerMetricsGenerator.plugin.task.registerComposeCompilerReportGenTaskForVariant
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension

fun Project.configureKotlinAndroidComposeCompilerReports(android: AndroidComponentsExtension<*, *, *>) {
    val commonExtension =
        runCatching { extensions.getByType(CommonExtension::class.java) }.getOrNull()

    android.onVariants { variant ->
        // Create gradle tasks for generating report
        registerComposeCompilerReportGenTaskForVariant(variant)
    }

    afterEvaluate {
        val isComposeEnabled = commonExtension?.buildFeatures?.compose

        if (isComposeEnabled != true) {
            error("Jetpack Compose is not found enabled in this module '$name'")
        }

        // When this method returns true it means gradle task for generating report is executing otherwise
        // normal compilation task is executing.
        val isFromReportGenGradleTask = executingComposeCompilerReportGenerationGradleTask()
        if (isFromReportGenGradleTask) {
            val kotlinAndroidExt = extensions.getByType<KotlinAndroidProjectExtension>()
            kotlinAndroidExt.target {
                // Exclude for test variants, no use!
                configureKotlinOptionsForComposeCompilerReport(compilations)
            }
        }
    }
}