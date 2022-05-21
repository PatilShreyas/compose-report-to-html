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

import dev.shreyaspatil.composeCompilerMetricsGenerator.core.model.Condition

/**
 * Model for holding Detail of a composable function
 *
 * @property functionName Name of a function
 * @property isRestartable States whether composable function is restartable or not
 * @property isSkippable States whether composable function is skippable or not
 * @property isInline States whether composable function is inline or not
 * @property params List of parameters of a composable function
 */
data class ComposableDetail(
    val functionName: String,
    val isRestartable: Boolean,
    val isSkippable: Boolean,
    val isInline: Boolean,
    val params: List<Parameter>
) {
    /**
     * Model for holding details of a parameter of a function
     *
     * @property condition Stability condition of a parameter
     * @property details Name and type details of a parameter
     */
    data class Parameter(val condition: Condition, val details: String) {
        private val nameAndType by lazy {
            details.split(":").map { it.trim() }.let { (name, type) -> name to type }
        }

        /**
         * Name of parameter
         */
        val name: String get() = nameAndType.first

        /**
         * Type of parameter
         */
        val type: String get() = nameAndType.second
    }
}
