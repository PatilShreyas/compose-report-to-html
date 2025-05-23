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

import dev.shreyaspatil.composeCompilerMetricsGenerator.core.exception.ParsingException
import dev.shreyaspatil.composeCompilerMetricsGenerator.core.model.Condition

/**
 * A model representing class reports
 *
 * @property classes All class details
 * @property errors List of Errors occurred while parsing the classes
 */
data class ClassesReport(
    val classes: List<ClassDetail>,
    val errors: List<ParsingException>,
) {
    private val stableAndUnstableClasses by lazy {
        classes.partition { it.stability === Condition.STABLE }
    }

    /**
     * List of stable classes
     */
    val stableClasses = stableAndUnstableClasses.first

    /**
     * List of unstable classes
     */
    val unstableClasses = stableAndUnstableClasses.second
}
