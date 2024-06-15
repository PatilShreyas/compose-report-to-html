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
package dev.shreyaspatil.composeCompilerMetricsGenerator.core.parser

import dev.shreyaspatil.composeCompilerMetricsGenerator.core.exception.ParsingException
import dev.shreyaspatil.composeCompilerMetricsGenerator.core.mapper.ConditionMapper
import dev.shreyaspatil.composeCompilerMetricsGenerator.core.model.RawContent
import dev.shreyaspatil.composeCompilerMetricsGenerator.core.model.classes.ClassDetail
import dev.shreyaspatil.composeCompilerMetricsGenerator.core.model.classes.ClassesReport

/**
 * Parses [ClassesReport] from the [String] content.
 */
object ClassReportParser : Parser<String, ClassesReport> {
    private val REGEX_RUNTIME_STABILITY = "<runtime stability> = (\\w+)".toRegex()
    private val REGEX_CLASS_NAME = "(stable|unstable|runtime) class (\\w*)".toRegex()
    private val REGEX_CLASS_FIELDS = "((\\w*) ((?:val|var) .*))".toRegex()

    /**
     * Parses all classes
     */
    override fun parse(content: String): ClassesReport {
        val errors = mutableListOf<ParsingException>()

        val classes =
            getClasses(content)
                .mapNotNull { classBody ->
                    runCatching {
                        parseClassDetail(classBody)
                    }.onFailure { cause ->
                        errors.add(ParsingException(classBody, cause))
                    }.getOrNull()
                }.toList()

        return ClassesReport(classes, errors.toList())
    }

    internal fun getClasses(content: String): List<String> {
        val lines = content.split("\n").filter { it.isNotBlank() }

        val classIndexes =
            lines.mapIndexedNotNull { index, s ->
                if (REGEX_CLASS_NAME.containsMatchIn(s)) {
                    index
                } else {
                    null
                }
            }

        return classIndexes.mapIndexed { index: Int, item: Int ->
            lines.subList(item, classIndexes.getOrElse(index + 1) { lines.size }).joinToString(separator = "\n")
        }
    }

    /**
     * Parses unit class
     */
    private fun parseClassDetail(classBody: String): ClassDetail {
        val classDetail = REGEX_CLASS_NAME.find(classBody)?.groupValues
        val className = classDetail?.getOrNull(2) ?: error("Undefined name for the class body: $classBody")
        val stability =
            classDetail.getOrNull(1)?.let { ConditionMapper.from(it) }
                ?: error("Undefined stability status for the class body: $classBody")

        val runtimeStability =
            REGEX_RUNTIME_STABILITY.find(classBody)?.groupValues?.getOrNull(1)?.let { ConditionMapper.from(it) }

        val fields =
            REGEX_CLASS_FIELDS.findAll(classBody).map { it.groupValues }.filter { it.isNotEmpty() }
                .map { ClassDetail.Field(it[2], it[3]) }.toList()

        return ClassDetail(
            className = className,
            stability = stability,
            runtimeStability = runtimeStability,
            fields = fields,
            rawContent = RawContent(classBody),
        )
    }
}
