package dev.shreyaspatil.composeCompilerMetricsGenerator.plugin

/*
 * Copyright 2020 Shreyas Patil
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.android.build.api.dsl.CommonExtension
import com.android.build.api.variant.AndroidComponentsExtension
import dev.shreyaspatil.composeCompilerMetricsGenerator.plugin.task.configureComposeCompilerReportGenTaskForVariant
import dev.shreyaspatil.composeCompilerMetricsGenerator.plugin.task.executingComposeCompilerReportGenerationGradleTask
import dev.shreyaspatil.composeCompilerMetricsGenerator.plugin.utils.kotlinOptions
import org.gradle.api.Plugin
import org.gradle.api.Project

@Suppress("UnstableApiUsage")
class ReportPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val reportExt = ComposeCompilerReportExtension.get(target)

        target.subprojects {
            afterEvaluate {
                val android = runCatching {
                    extensions.getByType(AndroidComponentsExtension::class.java)
                }.getOrNull()

                val commonExtension = runCatching { extensions.getByType(CommonExtension::class.java) }.getOrNull()
                val isComposeEnabled = commonExtension?.buildFeatures?.compose

                val isFromReportGenGradleTask = project.executingComposeCompilerReportGenerationGradleTask()
                if (isComposeEnabled == true && isFromReportGenGradleTask) {
                    commonExtension.configureKotlinOptionsForComposeCompilerReport(reportExt)
                }

                if (android != null && isComposeEnabled == true) {
                    android.onVariants { variant ->
                        configureComposeCompilerReportGenTaskForVariant(variant, reportExt)
                    }
                }
            }
        }
    }


    private fun CommonExtension<*, *, *, *>.configureKotlinOptionsForComposeCompilerReport(
        reportExtension: ComposeCompilerReportExtension,
    ) {
        kotlinOptions {
            val outputPath = reportExtension.outputPath.get()
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

