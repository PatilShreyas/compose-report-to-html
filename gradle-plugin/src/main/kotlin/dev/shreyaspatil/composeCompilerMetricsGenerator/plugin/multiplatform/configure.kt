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
@file:Suppress("ktlint:standard:filename")

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
    compilations
        .filter { compilation -> !compilation.name.endsWith("Test", ignoreCase = true) }
        .forEach {
            it.kotlinOptions {
                configureKotlinOptionsForComposeCompilerReport(this@configureKotlinOptionsForComposeCompilerReport)
            }
        }
}
