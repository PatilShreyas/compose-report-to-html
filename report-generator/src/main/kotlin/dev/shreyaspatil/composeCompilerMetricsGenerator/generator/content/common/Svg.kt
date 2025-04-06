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
package dev.shreyaspatil.composeCompilerMetricsGenerator.generator.content.common

import dev.shreyaspatil.composeCompilerMetricsGenerator.generator.style.setStyle
import kotlinx.html.FlowOrPhrasingContent
import kotlinx.html.HTMLTag
import kotlinx.html.HtmlInlineTag
import kotlinx.html.TagConsumer
import kotlinx.html.svg
import kotlinx.html.visit

/**
 * Acts as a SVG Icon HTML element
 */
@Suppress("ktlint:standard:function-naming")
fun FlowOrPhrasingContent.SvgIcon(
    width: String,
    height: String,
    viewBox: String,
    fill: String,
    fillRule: String,
    clipRule: String,
    d: String,
    pathFill: String,
) {
    svg {
        setStyle(width = width, height = height)
        attributes.run {
            this["viewBox"] = viewBox
            this["fill"] = fill
        }
        path {
            this.fillRule = fillRule
            this.clipRule = clipRule
            this.d = d
            this.fill = pathFill
        }
    }
}

/**
 * Represents as a `path` for SVG
 */
class SvgPath(
    consumer: TagConsumer<*>,
) : HTMLTag(
        tagName = "path",
        consumer = consumer,
        initialAttributes = emptyMap(),
        inlineTag = true,
        emptyTag = false,
    ),
    HtmlInlineTag {
    var fillRule: String
        get() = attributes["fill-rule"].toString()
        set(newValue) {
            attributes["fill-rule"] = newValue
        }

    var clipRule: String
        get() = attributes["clip-rule"].toString()
        set(newValue) {
            attributes["clip-rule"] = newValue
        }

    var d: String
        get() = attributes["d"].toString()
        set(newValue) {
            attributes["d"] = newValue
        }

    var fill: String
        get() = attributes["fill"].toString()
        set(newValue) {
            attributes["fill"] = newValue
        }
}

fun FlowOrPhrasingContent.path(block: SvgPath.() -> Unit = {}) {
    SvgPath(consumer).visit(block)
}
