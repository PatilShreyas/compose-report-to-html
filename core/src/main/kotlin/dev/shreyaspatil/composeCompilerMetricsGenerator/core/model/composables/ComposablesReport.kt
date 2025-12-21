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
package dev.shreyaspatil.composeCompilerMetricsGenerator.core.model.composables

import dev.shreyaspatil.composeCompilerMetricsGenerator.core.exception.ParsingException
import dev.shreyaspatil.composeCompilerMetricsGenerator.core.model.Condition

data class ComposablesReport(
    val composables: List<ComposableDetail>,
    val errors: List<ParsingException>,
) {
    /**
     * The composable functions which are restartable but not skippable.
     */
    val restartableButNotSkippableComposables: List<ComposableDetail>

    /**
     * The composable functions which are restartable and skippable but some of their parameters are unstable or has
     * missing stability.
     */
    val unstableParameterComposables: List<ComposableDetail>

    /**
     * The composable functions without any issues or are healthy.
     */
    val nonIssuesComposables: List<ComposableDetail>


    init {
        val restartableNotSkippable = mutableListOf<ComposableDetail>()
        val unstableParams = mutableListOf<ComposableDetail>()
        val nonIssues = mutableListOf<ComposableDetail>()

        composables.forEach { detail ->
            when {
                !detail.isSkippable && detail.isRestartable -> {
                    restartableNotSkippable.add(detail)
                }
                detail.params.any { it.condition != Condition.STABLE } -> {
                    unstableParams.add(detail)
                }
                else -> {
                    nonIssues.add(detail)
                }
            }
        }

        this.restartableButNotSkippableComposables = restartableNotSkippable
        this.unstableParameterComposables = unstableParams
        this.nonIssuesComposables = nonIssues
    }
}
