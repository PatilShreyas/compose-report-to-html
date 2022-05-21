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
import dev.shreyaspatil.composeCompilerMetricsGenerator.generator.content.common.CollapsibleContent
import dev.shreyaspatil.composeCompilerMetricsGenerator.generator.content.common.EmptyContent
import dev.shreyaspatil.composeCompilerMetricsGenerator.generator.style.setStyle
import kotlinx.html.FlowContent
import kotlinx.html.section
import kotlinx.html.table
import kotlinx.html.td
import kotlinx.html.th
import kotlinx.html.tr

fun FlowContent.DetailedStatistics(detailedStatistics: DetailedStatistics) {
    if (detailedStatistics.items.isEmpty()) {
        EmptyContent("No Detailed Statistics")
        return
    }
    section {
        CollapsibleContent("Detailed Statistics") {
            table {
                setStyle(width = "100%")

                tr {
                    detailedStatistics.headers.forEach { header ->
                        th { +header }
                    }
                }

                detailedStatistics.items.forEach {
                    tr {
                        it.item.map { it.value }.forEach { value ->
                            td { +value }
                        }
                    }
                }
            }
        }
    }
}
