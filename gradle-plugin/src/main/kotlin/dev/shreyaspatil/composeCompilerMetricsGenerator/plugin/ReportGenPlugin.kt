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
package dev.shreyaspatil.composeCompilerMetricsGenerator.plugin

import com.android.build.api.dsl.CommonExtension
import com.android.build.api.variant.AndroidComponentsExtension
import dev.shreyaspatil.composeCompilerMetricsGenerator.plugin.task.executingComposeCompilerReportGenerationGradleTask
import dev.shreyaspatil.composeCompilerMetricsGenerator.plugin.task.registerComposeCompilerReportGenTaskForJvmProject
import dev.shreyaspatil.composeCompilerMetricsGenerator.plugin.task.registerComposeCompilerReportGenTaskForTarget
import dev.shreyaspatil.composeCompilerMetricsGenerator.plugin.task.registerComposeCompilerReportGenTaskForVariant
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.compose.ComposeExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinCommonToolOptions
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

@Suppress("UnstableApiUsage")
class ReportGenPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val reportExt = ComposeCompilerReportExtension.create(target)

        val android =
            runCatching {
                target.extensions.getByType(AndroidComponentsExtension::class.java)
            }.getOrNull()
        val jvm =
            runCatching {
                target.extensions.getByType(KotlinJvmProjectExtension::class.java)
            }.getOrNull()
        val multiplatform =
            runCatching {
                target.extensions.getByType(KotlinMultiplatformExtension::class.java)
            }.getOrNull()
        val composeMultiplatform =
            runCatching {
                target.extensions.getByType(ComposeExtension::class.java)
            }.getOrNull()

        when {
            composeMultiplatform != null -> {
                when {
                    jvm != null -> { // if kotlin jvm is applied
                        target.configureKotlinJvmComposeCompilerReports(jvm)
                    }
                    multiplatform != null -> { // if kotlin multiplatform is applied
                        target.configureKotlinMultiplatformComposeCompilerReports(multiplatform)
                    }
                    android != null -> { // if kotlin android is applied
                        target.configureKotlinAndroidComposeCompilerReports(android)
                    }
                }
            }
            android != null -> {
                target.configureKotlinAndroidComposeCompilerReports(android)
            }
        }
    }

    private fun Project.configureKotlinJvmComposeCompilerReports(jvmExt: KotlinJvmProjectExtension) {
        // Create gradle tasks for generating report
        registerComposeCompilerReportGenTaskForJvmProject(jvmExt.target.project.name)

        afterEvaluate {
            // When this method returns true it means gradle task for generating report is executing otherwise
            // normal compilation task is executing.
            val isFromReportGenGradleTask = executingComposeCompilerReportGenerationGradleTask()
            if (isFromReportGenGradleTask) {
                jvmExt.target {
                    compilations.filter { !it.name.endsWith("Test") }.forEach {
                        it.kotlinOptions {
                            configureKotlinOptionsForComposeCompilerReport(this@configureKotlinJvmComposeCompilerReports)
                        }
                    }
                }
            }
        }
    }

    private fun Project.configureKotlinMultiplatformComposeCompilerReports(multiplatformExt: KotlinMultiplatformExtension) {
        // Create gradle tasks for generating report
        multiplatformExt.targets.configureEach {
            if (this.name == "metadata") return@configureEach

            if (this.name == "android") {
                // register a task for each build type
                registerComposeCompilerReportGenTaskForTarget(this, buildType = "Debug")
                registerComposeCompilerReportGenTaskForTarget(this, buildType = "Release")
            } else {
                registerComposeCompilerReportGenTaskForTarget(this)
            }
        }

        afterEvaluate {
            // When this method returns true it means gradle task for generating report is executing otherwise
            // normal compilation task is executing.
            val isFromReportGenGradleTask = executingComposeCompilerReportGenerationGradleTask()
            if (isFromReportGenGradleTask) {
                multiplatformExt.targets.flatMap { it.compilations }
                    .filter { !it.name.endsWith("Test", ignoreCase = true) }
                    .forEach {
                        it.kotlinOptions {
                            configureKotlinOptionsForComposeCompilerReport(this@configureKotlinMultiplatformComposeCompilerReports)
                        }
                    }
            }
        }
    }

    private fun Project.configureKotlinAndroidComposeCompilerReports(androidExt: AndroidComponentsExtension<*, *, *>) {
        val commonExtension = runCatching { extensions.getByType(CommonExtension::class.java) }.getOrNull()

        androidExt.onVariants { variant ->
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
                    compilations.filter { !it.name.endsWith("Test") }.forEach {
                        it.kotlinOptions {
                            configureKotlinOptionsForComposeCompilerReport(this@afterEvaluate)
                        }
                    }
                }
            }
        }
    }

    private fun KotlinCommonToolOptions.configureKotlinOptionsForComposeCompilerReport(project: Project) {
        val reportExtension = project.extensions.getByType<ComposeCompilerReportExtension>()
        val outputPath = reportExtension.composeRawMetricsOutputDirectory.absolutePath
        if (reportExtension.enableReport.get()) {
            freeCompilerArgs +=
                listOf(
                    "-P",
                    "plugin:androidx.compose.compiler.plugins.kotlin:reportsDestination=$outputPath",
                )
        }
        if (reportExtension.enableMetrics.get()) {
            freeCompilerArgs +=
                listOf(
                    "-P",
                    "plugin:androidx.compose.compiler.plugins.kotlin:metricsDestination=$outputPath",
                )
        }
    }
}
