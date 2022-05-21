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
package dev.shreyaspatil.composeCompilerMetricsGenerator.core

import com.squareup.moshi.Moshi
import com.squareup.moshi.adapter
import dev.shreyaspatil.composeCompilerMetricsGenerator.core.model.DetailedStatistics
import dev.shreyaspatil.composeCompilerMetricsGenerator.core.model.Item
import dev.shreyaspatil.composeCompilerMetricsGenerator.core.model.RowItems
import dev.shreyaspatil.composeCompilerMetricsGenerator.core.model.classes.ClassesReport
import dev.shreyaspatil.composeCompilerMetricsGenerator.core.model.composables.ComposablesReport
import dev.shreyaspatil.composeCompilerMetricsGenerator.core.parser.ClassReportParser
import dev.shreyaspatil.composeCompilerMetricsGenerator.core.parser.ComposableReportParser
import java.nio.file.Paths
import kotlin.io.path.absolutePathString

/**
 * Provides metrics and reports of a Compose compiler
 */
interface ComposeCompilerMetricsProvider {
    /**
     * Returns key-value pairs from composable metrics
     */
    fun getOverallStatistics(): Map<String, Long>

    /**
     * Returns detailed statistics from composable report
     */
    fun getDetailedStatistics(): DetailedStatistics

    /**
     * Returns metrics for the composable functions.
     */
    fun getComposablesReport(): ComposablesReport

    /**
     * Returns metrics for the classes.
     */
    fun getClassesReport(): ClassesReport
}

/**
 * Default implementation for [ComposeCompilerMetricsProvider] which parses content provided by
 * [ComposeMetricsFileProvider].
 */
@OptIn(ExperimentalStdlibApi::class)
private class DefaultComposeCompilerMetricsProvider(
    private val contentProvider: ComposeMetricsContentProvider
) : ComposeCompilerMetricsProvider {
    private val moshi = Moshi.Builder().build()

    override fun getOverallStatistics(): Map<String, Long> {
        val statistics = moshi.adapter<Map<String, Long>>().fromJson(contentProvider.briefStatisticsContents)
        return statistics?.map { (name, value) -> camelCaseToWord(name) to value }?.toMap() ?: emptyMap()
    }

    override fun getDetailedStatistics(): DetailedStatistics {
        val csv = contentProvider.detailedStatisticsCsvRows

        val metrics = if (csv.size > 1) {
            val headers = splitWithCsvSeparator(csv.first())

            csv.subList(1, csv.lastIndex)
                .map { splitWithCsvSeparator(it) }
                .map { items -> RowItems(items.mapIndexed { index, value -> Item(headers[index], value) }) }
        } else {
            emptyList()
        }

        return DetailedStatistics(metrics)
    }

    override fun getComposablesReport(): ComposablesReport {
        return ComposableReportParser.parse(contentProvider.composablesReportContents)
    }

    override fun getClassesReport(): ClassesReport {
        return ClassReportParser.parse(contentProvider.classesReportContents)
    }

    private fun splitWithCsvSeparator(content: String) = content.split(",").filter { it.isNotBlank() }

    private fun camelCaseToWord(content: String): String =
        content.replace(REGEX_CAMEL_CASE) { " ${it.value[0].uppercase()}" }.trim()

    companion object {
        private val REGEX_CAMEL_CASE = "(\\A[a-z]|[A-Z])".toRegex()
    }
}

/**
 * Factory function for creating [ComposeCompilerMetricsProvider].
 */
fun ComposeCompilerMetricsProvider(
    briefStatisticsJsonFilePath: String,
    detailedStatisticsJsonFilePath: String,
    composableReportFilePath: String,
    classesReportFilePath: String
): ComposeCompilerMetricsProvider = DefaultComposeCompilerMetricsProvider(
    ComposeMetricsContentProvider(
        ComposeMetricsFileProvider(
            briefStatisticsJsonFilePath = Paths.get(briefStatisticsJsonFilePath).absolutePathString(),
            detailedStatisticsCsvFilePath = Paths.get(detailedStatisticsJsonFilePath).absolutePathString(),
            composableReportFilePath = Paths.get(composableReportFilePath).absolutePathString(),
            classesReportFilePath = Paths.get(classesReportFilePath).absolutePathString()
        )
    )
)
