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
import dev.shreyaspatil.composeCompilerMetricsGenerator.core.model.classes.ClassDetail
import dev.shreyaspatil.composeCompilerMetricsGenerator.core.model.classes.ClassesReport
import dev.shreyaspatil.composeCompilerMetricsGenerator.generator.content.common.CollapsibleContent
import dev.shreyaspatil.composeCompilerMetricsGenerator.generator.content.common.EmptyContent
import dev.shreyaspatil.composeCompilerMetricsGenerator.generator.style.Colors
import dev.shreyaspatil.composeCompilerMetricsGenerator.generator.style.setStyle
import dev.shreyaspatil.composeCompilerMetricsGenerator.generator.style.statusCssClass
import dev.shreyaspatil.composeCompilerMetricsGenerator.generator.utils.forEachIndexedFromOne
import kotlinx.html.FlowContent
import kotlinx.html.br
import kotlinx.html.div
import kotlinx.html.h3
import kotlinx.html.section
import kotlinx.html.span
import kotlinx.html.table
import kotlinx.html.td
import kotlinx.html.th
import kotlinx.html.tr

fun FlowContent.ClassesReport(
    includeStableClasses: Boolean,
    report: ClassesReport,
) {
    if (report.classes.isEmpty()) {
        EmptyContent("No Class Report")
        return
    }
    section {
        div {
            CollapsibleContent("Classes Report") {
                if (report.unstableClasses.isNotEmpty()) {
                    CollapsibleContent(
                        summary = "Unstable Classes",
                        summaryAttr = {
                            setStyle(
                                backgroundColor = Colors.RED_DARK,
                                fontSize = "18px",
                            )
                        },
                    ) {
                        setStyle(backgroundColor = Colors.PINK_LIGHT)
                        ClassesReport(report.unstableClasses)
                    }
                } else {
                    EmptyContent("No Unstable classes found 😁")
                }

                if (report.stableClasses.isNotEmpty() && includeStableClasses) {
                    CollapsibleContent(
                        summary = "Stable Classes",
                        summaryAttr = {
                            setStyle(
                                backgroundColor = Colors.GREEN_DARK,
                                fontSize = "18px",
                            )
                        },
                    ) {
                        setStyle(backgroundColor = Colors.ALABASTER)
                        ClassesReport(report.stableClasses)
                    }
                } else {
                    br { }
                    val message = if (!includeStableClasses) {
                        "Report for stable classes is disabled in the options"
                    } else {
                        "No Stable classes found"
                    }
                    EmptyContent(message)
                }
            }
        }
    }
}

fun FlowContent.ClassesReport(classes: List<ClassDetail>) = table {
    classes.forEachIndexedFromOne { index, detail ->
        tr {
            td {
                +"$index."
            }
            td {
                h3 {
                    span(statusCssClass(detail.stability === Condition.STABLE)) {
                        when (detail.stability) {
                            Condition.STABLE -> +"✅ Stable"
                            Condition.UNSTABLE -> +"❌ Unstable"
                            else -> +"Missing"
                        }
                    }
                    span("code") { +" class ${detail.className}" }
                }
                table {
                    if (detail.fields.isNotEmpty()) {
                        tr {
                            th { +"No." }
                            th { +"Status" }
                            th { +"Field" }
                            th { +"Type" }
                        }
                        detail.fields.forEachIndexedFromOne { index, field ->
                            tr {
                                setStyle(
                                    backgroundColor = statusColorFrom(field.status),
                                    color = textColorFrom(field.status),
                                )
                                td { +index.toString() }
                                td { +field.status.uppercase() }
                                td("code") { +field.name }
                                td("code") { +field.type }
                            }
                        }
                    }
                }
            }
        }
    }
}

fun statusColorFrom(status: String) = when (status.lowercase()) {
    "stable" -> Colors.GREEN_LIGHT
    "unstable" -> Colors.RED_DARK
    else -> Colors.YELLOW
}

fun textColorFrom(status: String) = when (status.lowercase()) {
    "unstable" -> Colors.WHITE
    else -> Colors.BLACK
}
