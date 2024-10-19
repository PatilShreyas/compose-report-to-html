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

import dev.shreyaspatil.composeCompilerMetricsGenerator.core.model.DetailedStatistics
import dev.shreyaspatil.composeCompilerMetricsGenerator.core.model.Item
import dev.shreyaspatil.composeCompilerMetricsGenerator.core.model.RowItems
import dev.shreyaspatil.composeCompilerMetricsGenerator.core.model.classes.ClassesReport
import dev.shreyaspatil.composeCompilerMetricsGenerator.core.model.composables.ComposablesReport
import dev.shreyaspatil.composeCompilerMetricsGenerator.core.parser.ClassReportParser
import dev.shreyaspatil.composeCompilerMetricsGenerator.core.parser.ComposableReportParser
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.longOrNull
import java.nio.file.Files

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
 * [ComposeCompilerRawReportProvider].
 */
private class DefaultComposeCompilerMetricsProvider(
    private val contentProvider: ComposeMetricsContentProvider,
) : ComposeCompilerMetricsProvider {
    override fun getOverallStatistics(): Map<String, Long> {
        val statistics = mutableMapOf<String, Long>()
        contentProvider.briefStatisticsContents.forEach { statContent ->
            val stats =
                Json
                    .decodeFromString<JsonObject>(statContent)
                    .mapNotNull { entry ->
                        // In Compose 1.7.0, now JSON also includes the details of compiler features.
                        // To avoid deserialization issues, we have to skip adding these details in this report and just
                        // have to take primitive values (i.e. actually metrics) in the account.
                        val primitive = entry.value as? JsonPrimitive
                        primitive?.longOrNull?.let { longValue ->
                            entry.key to longValue
                        }
                    }

            if (statistics.isEmpty()) {
                statistics.putAll(stats)
            } else {
                stats.forEach { (key, value) ->
                    statistics[key] = statistics[key]?.plus(value) ?: value
                }
            }
        }
        return statistics.toMap()
    }

    override fun getDetailedStatistics(): DetailedStatistics {
        val csv = contentProvider.detailedStatisticsCsvRows

        val metrics =
            if (csv.size > 1) {
                val headers = splitWithCsvSeparator(csv.first())

                csv.subList(1, csv.size)
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
}

/**
 * Factory function for creating [ComposeCompilerMetricsProvider].
 */
fun ComposeCompilerMetricsProvider(files: ComposeCompilerRawReportProvider): ComposeCompilerMetricsProvider {
    val contentProvider = ComposeMetricsContentProvider(files)
    return DefaultComposeCompilerMetricsProvider(contentProvider)
}

fun main() {
    val json =
        """
        {
          "skippableComposables" : 110,
          "restartableComposables" : 146,
          "readonlyComposables" : 0,
          "totalComposables" : 154,
          "restartGroups" : 146,
          "totalGroups" : 165,
          "staticArguments" : 217,
          "certainArguments" : 163,
          "knownStableArguments" : 1363,
          "knownUnstableArguments" : 31,
          "unknownStableArguments" : 2,
          "totalArguments" : 1396,
          "markedStableClasses" : 2,
          "inferredStableClasses" : 8,
          "inferredUnstableClasses" : 2,
          "inferredUncertainClasses" : 0,
          "effectivelyStableClasses" : 10,
          "totalClasses" : 12,
          "memoizedLambdas" : 103,
          "singletonLambdas" : 14,
          "singletonComposableLambdas" : 25,
          "composableLambdas" : 69,
          "totalLambdas" : 122,
          "featureFlags" : {
            "StrongSkipping" : true
          }
        }
        """.trimIndent()

    val ss =
        DefaultComposeCompilerMetricsProvider(
            ComposeMetricsContentProvider(
                ComposeCompilerRawReportProvider.FromIndividualFiles(
                    listOf(
                        Files.createTempFile("dfdfd", "dsdsd").toFile().apply {
                            writeText(json)
                        },
                    ),
                    emptyList(),
                    emptyList(),
                    emptyList(),
                ),
            ),
        )

    println(ss.getOverallStatistics())
}
