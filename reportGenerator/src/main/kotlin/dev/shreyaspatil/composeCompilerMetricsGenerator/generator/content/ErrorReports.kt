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

import dev.shreyaspatil.composeCompilerMetricsGenerator.core.exception.ParsingException
import dev.shreyaspatil.composeCompilerMetricsGenerator.generator.content.common.CollapsibleContent
import dev.shreyaspatil.composeCompilerMetricsGenerator.generator.style.Colors
import dev.shreyaspatil.composeCompilerMetricsGenerator.generator.style.setStyle
import dev.shreyaspatil.composeCompilerMetricsGenerator.generator.utils.forEachIndexedFromOne
import dev.shreyaspatil.composeCompilerMetricsGenerator.generator.utils.lines
import kotlinx.html.FlowContent
import kotlinx.html.a
import kotlinx.html.b
import kotlinx.html.br
import kotlinx.html.i
import kotlinx.html.p

fun FlowContent.ErrorReports(errors: List<ParsingException>) {
    if (errors.isNotEmpty()) {
        i {
            +"These below errors were occurred while generating this report"
            a(
                href = "https://github.com/PatilShreyas/compose-report-to-html/issues",
                target = "_blank"
            ) { +" Please report issues here" }
        }
        errors.forEachIndexedFromOne { index, error ->
            CollapsibleContent(
                summary = "$index. ${error.message ?: "Unknown error"}",
                summaryAttr = {
                    setStyle(backgroundColor = Colors.RED_DARK, color = Colors.WHITE, margin = "4px", fontSize = "14px")
                }
            ) {
                p("code") {
                    setStyle(
                        backgroundColor = Colors.PINK_LIGHT,
                        fontSize = "14px",
                        color = Colors.BLACK,
                        textAlign = "left",
                        padding = "8px"
                    )

                    error.stackTraceToString().lines().forEach { line ->
                        +line
                        br { }
                    }

                    br { }
                    b { +"The content was:" }

                    br { }
                    error.content.lines().forEach { line ->
                        +line
                        br { }
                    }
                }
            }
        }
    }
}
