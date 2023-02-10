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
import dev.shreyaspatil.composeCompilerMetricsGenerator.plugin.task.createComposeCompilerReportGenTaskForVariant
import dev.shreyaspatil.composeCompilerMetricsGenerator.plugin.task.executingComposeCompilerReportGenerationGradleTask
import dev.shreyaspatil.composeCompilerMetricsGenerator.plugin.utils.kotlinOptions
import org.gradle.api.Plugin
import org.gradle.api.Project

@Suppress("UnstableApiUsage")
class ReportGenPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val reportExt = ComposeCompilerReportExtension.get(target)

        val android = runCatching {
            target.extensions.getByType(AndroidComponentsExtension::class.java)
        }.getOrNull()

        val commonExtension = runCatching { target.extensions.getByType(CommonExtension::class.java) }.getOrNull()

        android?.onVariants { variant ->
            // Create gradle tasks for generating report
            target.createComposeCompilerReportGenTaskForVariant(variant, reportExt)
        }

        target.afterEvaluate {
            val isComposeEnabled = commonExtension?.buildFeatures?.compose
                ?: error("Jetpack Compose is not found enabled in this module '$name'")

            // When this method returns true it means gradle task for generating report is executing otherwise
            // normal compilation task is executing.
            val isFromReportGenGradleTask = executingComposeCompilerReportGenerationGradleTask()
            if (isComposeEnabled && isFromReportGenGradleTask) {
                commonExtension.configureKotlinOptionsForComposeCompilerReport(reportExt)
            }
        }
    }

    private fun CommonExtension<*, *, *, *>.configureKotlinOptionsForComposeCompilerReport(
        reportExtension: ComposeCompilerReportExtension,
    ) {
        kotlinOptions {
            val outputPath = reportExtension.composeRawMetricsOutputDirectory.absolutePath
            if (reportExtension.enableReport.get()) {
                freeCompilerArgs += listOf(
                    "-P",
                    "plugin:androidx.compose.compiler.plugins.kotlin:reportsDestination=$outputPath"
                )
            }
            if (reportExtension.enableMetrics.get()) {
                freeCompilerArgs += listOf(
                    "-P",
                    "plugin:androidx.compose.compiler.plugins.kotlin:metricsDestination=$outputPath"
                )
            }
        }
    }
}
