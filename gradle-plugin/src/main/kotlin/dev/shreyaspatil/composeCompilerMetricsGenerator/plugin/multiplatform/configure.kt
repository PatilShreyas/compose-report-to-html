package dev.shreyaspatil.composeCompilerMetricsGenerator.plugin.multiplatform

import com.android.build.api.variant.AndroidComponentsExtension
import dev.shreyaspatil.composeCompilerMetricsGenerator.plugin.configureKotlinOptionsForComposeCompilerReport
import dev.shreyaspatil.composeCompilerMetricsGenerator.plugin.task.executingComposeCompilerReportGenerationGradleTask
import dev.shreyaspatil.composeCompilerMetricsGenerator.plugin.task.registerComposeCompilerReportGenTaskForTarget
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation

fun Project.configureKotlinMultiplatformComposeCompilerReports(multiplatformExt: KotlinMultiplatformExtension) {
    // Create gradle tasks for generating report
    multiplatformExt.targets.configureEach {
        if (this.name == "metadata") return@configureEach

        if (this.name == "android") {
            // register a task for each variant
            val androidExt = extensions.getByType(AndroidComponentsExtension::class.java)
            androidExt.onVariants { variant ->
                registerComposeCompilerReportGenTaskForTarget(this, variant)
            }
        } else {
            registerComposeCompilerReportGenTaskForTarget(this)
        }
    }

    afterEvaluate {
        // When this method returns true it means gradle task for generating report is executing otherwise
        // normal compilation task is executing.
        val isFromReportGenGradleTask = executingComposeCompilerReportGenerationGradleTask()
        if (isFromReportGenGradleTask) {
            configureKotlinOptionsForComposeCompilerReport(multiplatformExt.targets.flatMap { it.compilations })
        }
    }
}

fun Project.configureKotlinOptionsForComposeCompilerReport(compilations: Collection<KotlinCompilation<*>>) {
    compilations.filter { compilation -> !compilation.name.endsWith("Test", ignoreCase = true) }
        .forEach {
            it.kotlinOptions {
                configureKotlinOptionsForComposeCompilerReport(this@configureKotlinOptionsForComposeCompilerReport)
            }
        }
}
