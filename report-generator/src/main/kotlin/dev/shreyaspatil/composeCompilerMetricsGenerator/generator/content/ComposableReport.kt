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
package dev.shreyaspatil.composeCompilerMetricsGenerator.generator.content

import dev.shreyaspatil.composeCompilerMetricsGenerator.core.model.Condition
import dev.shreyaspatil.composeCompilerMetricsGenerator.core.model.composables.ComposableDetail
import dev.shreyaspatil.composeCompilerMetricsGenerator.core.model.composables.ComposablesReport
import dev.shreyaspatil.composeCompilerMetricsGenerator.generator.content.common.CollapsibleContent
import dev.shreyaspatil.composeCompilerMetricsGenerator.generator.content.common.EmptyContent
import dev.shreyaspatil.composeCompilerMetricsGenerator.generator.style.Colors
import dev.shreyaspatil.composeCompilerMetricsGenerator.generator.style.setStyle
import dev.shreyaspatil.composeCompilerMetricsGenerator.generator.utils.forEachIndexedFromOne
import kotlinx.html.BODY
import kotlinx.html.FlowContent
import kotlinx.html.br
import kotlinx.html.h3
import kotlinx.html.h4
import kotlinx.html.section
import kotlinx.html.span
import kotlinx.html.table
import kotlinx.html.td
import kotlinx.html.th
import kotlinx.html.tr

fun BODY.ComposablesReport(composablesReport: ComposablesReport) {
    if (composablesReport.composables.isEmpty()) {
        EmptyContent("No Composables Report")
        return
    }
    section {
        CollapsibleContent("Composables Report") {
            if (composablesReport.restartableButNotSkippableComposables.isNotEmpty()) {
                CollapsibleContent(
                    summary = "Composables with issues (Restartable but Not Skippable)",
                    summaryAttr = {
                        setStyle(backgroundColor = Colors.RED_DARK, fontSize = "18px")
                    },
                ) {
                    setStyle(backgroundColor = Colors.PINK_LIGHT)
                    ComposablesReport(composablesReport.restartableButNotSkippableComposables)
                }
            } else {
                EmptyContent("No composable found with issues")
            }

            if (composablesReport.nonIssuesComposables.isNotEmpty()) {
                CollapsibleContent(
                    summary = "Composibles without issues",
                    summaryAttr = {
                        setStyle(backgroundColor = Colors.GREEN_DARK, fontSize = "18px")
                    },
                ) {
                    setStyle(backgroundColor = Colors.ALABASTER)
                    ComposablesReport(composablesReport.nonIssuesComposables)
                }
            } else {
                br { }
                EmptyContent("No composable found without any issues")
            }
        }
    }
}

fun FlowContent.ComposablesReport(composables: List<ComposableDetail>) = table {
    composables.forEachIndexedFromOne { index, detail ->
        tr {
            td {
                +"$index."
            }
            td {
                h3("code") {
                    if (detail.isInline) {
                        +"inline"
                    }
                    span { +" fun ${detail.functionName}" }
                }
                h4 {
                    span(if (detail.isSkippable) "status-success" else "status-failure") {
                        if (detail.isSkippable) {
                            +"✅ Skippable"
                        } else {
                            +"❌ Non Skippable"
                        }
                    }
                    span(if (detail.isRestartable) "status-success" else "status-failure") {
                        if (detail.isRestartable) {
                            +" ✅ Restartable"
                        } else {
                            +" ❌ Non Restartable"
                        }
                    }
                }
                table {
                    if (detail.params.isNotEmpty()) {
                        tr {
                            th { +"No." }
                            th { +"Stability" }
                            th { +"Parameter" }
                            th { +"Type" }
                        }
                        detail.params.forEachIndexedFromOne { index, param ->
                            tr(
                                when (param.condition) {
                                    Condition.STABLE -> "background-status-success"
                                    Condition.UNSTABLE -> "background-status-failure"
                                    Condition.MISSING -> "background-status-missing"
                                },
                            ) {
                                td { +index.toString() }
                                td { +param.condition.toString() }
                                td("code") { +param.name }
                                td("code") { +param.type }
                            }
                        }
                    }
                }
            }
        }
    }
}
