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

import dev.shreyaspatil.composeCompilerMetricsGenerator.core.model.DetailedStatistics
import dev.shreyaspatil.composeCompilerMetricsGenerator.core.model.classes.ClassesReport
import dev.shreyaspatil.composeCompilerMetricsGenerator.core.model.composables.ComposablesReport
import dev.shreyaspatil.composeCompilerMetricsGenerator.generator.ReportSpec
import dev.shreyaspatil.composeCompilerMetricsGenerator.generator.script.CollapsibleScript
import dev.shreyaspatil.composeCompilerMetricsGenerator.generator.style.FontsLinking
import dev.shreyaspatil.composeCompilerMetricsGenerator.generator.style.PageStyle
import kotlinx.html.HTML
import kotlinx.html.body
import kotlinx.html.h1
import kotlinx.html.head
import kotlinx.html.hr
import kotlinx.html.title

@Suppress("ktlint:standard:function-naming")
fun HTML.MainContent(
    reportSpec: ReportSpec,
    overallStatistics: Map<String, Long>,
    detailedStatistics: DetailedStatistics,
    composablesReport: ComposablesReport,
    classesReport: ClassesReport,
) {
    val options = reportSpec.options
    head {
        title("${reportSpec.name} - Compose Compiler Report")
        FontsLinking()
        PageStyle()
    }
    body {
        h1 { +"Compose Compiler Report - ${reportSpec.name}" }
        if (options.showOnlyUnstableComposables) {
            OnlyUnstableComposables(composablesReport)
        } else {
            BriefStatistics(overallStatistics)
            hr { }
            DetailedStatistics(detailedStatistics)
            hr { }
            ComposablesReport(
                includeStableComposables = options.includeStableComposables,
                composablesReport = composablesReport,
            )
            hr { }
            if (options.includeClasses) {
                ClassesReport(options.includeStableClasses, classesReport)
                hr { }
            }
        }
        ErrorReports(composablesReport.errors + classesReport.errors)
        Footer()
        CollapsibleScript()
    }
}
