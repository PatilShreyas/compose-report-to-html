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

import com.android.build.api.variant.AndroidComponentsExtension
import dev.shreyaspatil.composeCompilerMetricsGenerator.plugin.multiplatform.android.configureKotlinAndroidComposeCompilerReports
import dev.shreyaspatil.composeCompilerMetricsGenerator.plugin.multiplatform.configureKotlinMultiplatformComposeCompilerReports
import dev.shreyaspatil.composeCompilerMetricsGenerator.plugin.multiplatform.jvm.configureKotlinJvmComposeCompilerReports
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.KotlinCommonToolOptions
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

@Suppress("UnstableApiUsage")
class ReportGenPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val reportExt = ComposeCompilerReportExtension.create(target)
        var isAppliedForKmp = false

        with(target) {
            pluginManager.withPlugin("org.jetbrains.compose") {
                isAppliedForKmp = true
                pluginManager.withPlugin("org.jetbrains.kotlin.jvm") {
                    // if kotlin jvm is applied
                    val jvm = extensions.getByType(KotlinJvmProjectExtension::class.java)
                    configureKotlinJvmComposeCompilerReports(jvm)
                }
                pluginManager.withPlugin("org.jetbrains.kotlin.multiplatform") {
                    // if kotlin multiplatform is applied
                    val multiplatform = extensions.getByType(KotlinMultiplatformExtension::class.java)
                    configureKotlinMultiplatformComposeCompilerReports(multiplatform)
                }
                pluginManager.withPlugin("org.jetbrains.kotlin.android") {
                    // if kotlin android is applied
                    val android = extensions.getByType(AndroidComponentsExtension::class.java)
                    configureKotlinAndroidComposeCompilerReports(android)
                }
            }
            var isAppliedForAndroid = false
            if (!isAppliedForKmp) {
                val android = extensions.getByType(AndroidComponentsExtension::class.java)
                pluginManager.withPlugin("com.android.application") {
                    configureKotlinAndroidComposeCompilerReports(android)
                    isAppliedForAndroid = true
                }
                pluginManager.withPlugin("com.android.library") {
                    configureKotlinAndroidComposeCompilerReports(android)
                    isAppliedForAndroid = true
                }
            }
            if (!isAppliedForAndroid && !isAppliedForKmp) {
                error("Jetpack Compose is not found enabled in this module '$name'")
            }
        }
    }
}

fun KotlinCommonToolOptions.configureKotlinOptionsForComposeCompilerReport(project: Project) {
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
