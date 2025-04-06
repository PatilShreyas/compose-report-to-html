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
package dev.shreyaspatil.composeCompilerMetricsGenerator.core.model.classes

import dev.shreyaspatil.composeCompilerMetricsGenerator.core.model.Condition
import dev.shreyaspatil.composeCompilerMetricsGenerator.core.model.RawContent

/**
 * Model for holding class details
 *
 * @property className Name of a class
 * @property stability Stability of a class
 * @property runtimeStability Runtime stability of a class
 * @property fields List of member fields of a class
 */
data class ClassDetail(
    val className: String,
    val stability: Condition,
    val runtimeStability: Condition?,
    val fields: List<Field>,
    val rawContent: RawContent,
) {
    /**
     * Model for holding field details of a class
     *
     * @property status Status of a field. E.g. STABLE, UNSTABLE, etc
     * @property details Name and type details of a field.
     */
    data class Field(
        val status: String,
        val details: String,
    ) {
        private val nameAndType by lazy {
            details
                .split(":")
                .map { it.trim() }
                .let { (name, type) -> name to type }
        }

        /**
         * Name of a field
         */
        val name: String get() = nameAndType.first

        /**
         * Type of field
         */
        val type: String get() = nameAndType.second
    }
}
