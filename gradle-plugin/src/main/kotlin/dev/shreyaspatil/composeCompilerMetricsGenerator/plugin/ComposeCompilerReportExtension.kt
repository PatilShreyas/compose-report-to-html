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

import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.getByType
import java.io.File

interface ComposeCompilerReportExtension {

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

    /**
     * Include the report for stable composable functions in the module which doesn't need optimizations.
     */
    val includeStableComposables: Property<Boolean>

    /**
     * Include the report for stable classes in the module which doesn't need optimizations.
     */
    val includeStableClasses: Property<Boolean>

    /**
     * Include the report for all classes in the module.
     */
    val includeClasses: Property<Boolean>

    val composeRawMetricsOutputDirectory: File
        get() = outputDirectory.get().resolve("raw")

    companion object {
        const val NAME = "htmlComposeCompilerReport"

        /**
         * Creates a extension of type [ComposeCompilerReportExtension] and returns
         */
        fun create(target: Project) = target.extensions.create<ComposeCompilerReportExtension>(NAME).apply {
            enableReport.convention(true)
            enableMetrics.convention(true)
            includeStableComposables.convention(false)
            includeStableClasses.convention(false)
            includeClasses.convention(true)
            name.convention("${target.rootProject.name}:${target.name}")
            outputDirectory.convention(target.buildDir.resolve("compose_report"))
        }

        /**
         * Get extensions applied to the [target] project.
         */
        fun get(target: Project) = target.extensions.getByType<ComposeCompilerReportExtension>()
    }
}
