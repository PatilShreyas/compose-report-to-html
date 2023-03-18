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
package dev.shreyaspatil.composeCompilerMetricsGenerator.generator

import dev.shreyaspatil.composeCompilerMetricsGenerator.core.ComposeCompilerMetricsProvider
import dev.shreyaspatil.composeCompilerMetricsGenerator.core.model.DetailedStatistics
import dev.shreyaspatil.composeCompilerMetricsGenerator.core.model.classes.ClassesReport
import dev.shreyaspatil.composeCompilerMetricsGenerator.core.model.composables.ComposablesReport
import dev.shreyaspatil.composeCompilerMetricsGenerator.core.utils.camelCaseToWord
import dev.shreyaspatil.composeCompilerMetricsGenerator.generator.content.MainContent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.html.html
import kotlinx.html.stream.createHTML

/**
 * Generates HTML content of a report using [metricsProvider]
 */
class HtmlReportGenerator(
    private val reportSpec: ReportSpec,
    private val metricsProvider: ComposeCompilerMetricsProvider,
) {
    /**
     * Returns HTML content as a [String]
     */
    fun generateHtml(): String = runBlocking(Dispatchers.Default) {
        val deferredOverallStatistics = async {
            metricsProvider.getOverallStatistics().map { (name, value) -> camelCaseToWord(name) to value }.toMap()
        }
        val deferredDetailedStatistics = async { metricsProvider.getDetailedStatistics() }
        val deferredComposablesReport = async { metricsProvider.getComposablesReport() }
        val deferredClassesReport = async { metricsProvider.getClassesReport() }

        val overallStatistics = deferredOverallStatistics.await()
        val detailedStatistics = deferredDetailedStatistics.await()
        val composablesReport = deferredComposablesReport.await()
        val classesReport = deferredClassesReport.await()

        return@runBlocking reportPageHtml(overallStatistics, detailedStatistics, composablesReport, classesReport)
    }

    private fun reportPageHtml(
        overallStatistics: Map<String, Long>,
        detailedStatistics: DetailedStatistics,
        composablesReport: ComposablesReport,
        classesReport: ClassesReport,
    ) = createHTML().html {
        MainContent(
            reportSpec = reportSpec,
            overallStatistics = overallStatistics,
            detailedStatistics = detailedStatistics,
            composablesReport = composablesReport,
            classesReport = classesReport,
        )
    }
}
