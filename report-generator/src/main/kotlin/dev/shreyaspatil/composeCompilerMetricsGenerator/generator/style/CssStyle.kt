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
package dev.shreyaspatil.composeCompilerMetricsGenerator.generator.style

import kotlinx.html.HTMLTag
import kotlinx.html.impl.DelegatingMap

object StyleProps {
    const val Color = "color"
    const val BackgroundColor = "background-color"
    const val FontSize = "font-size"
    const val TextAlign = "text-align"
    const val Width = "width"
    const val Margin = "margin"
    const val Padding = "padding"
}

fun HTMLTag.setStyle(
    color: String? = null,
    backgroundColor: String? = null,
    fontSize: String? = null,
    textAlign: String? = null,
    width: String? = null,
    margin: String? = null,
    padding: String? = null,
) {
    attributes.setStyle(
        color = color,
        backgroundColor = backgroundColor,
        fontSize = fontSize,
        textAlign = textAlign,
        width = width,
        margin = margin,
        padding = padding
    )
}

fun DelegatingMap.setStyle(
    color: String? = null,
    backgroundColor: String? = null,
    fontSize: String? = null,
    textAlign: String? = null,
    width: String? = null,
    margin: String? = null,
    padding: String? = null,
) {
    set(
        "style",
        buildStyle(
            color = color,
            backgroundColor = backgroundColor,
            fontSize = fontSize,
            textAlign = textAlign,
            width = width,
            margin = margin,
            padding = padding
        )
    )
}

fun buildStyle(
    color: String? = null,
    backgroundColor: String? = null,
    fontSize: String? = null,
    textAlign: String? = null,
    width: String? = null,
    margin: String? = null,
    padding: String? = null,
): String = buildString {
    color?.let { append(styleProperty(StyleProps.Color, color)) }
    backgroundColor?.let { append(styleProperty(StyleProps.BackgroundColor, backgroundColor)) }
    fontSize?.let { append(styleProperty(StyleProps.FontSize, fontSize)) }
    textAlign?.let { append(styleProperty(StyleProps.TextAlign, textAlign)) }
    width?.let { append(styleProperty(StyleProps.Width, width)) }
    margin?.let { append(styleProperty(StyleProps.Margin, margin)) }
    padding?.let { append(styleProperty(StyleProps.Padding, padding)) }
}

fun styleProperty(property: String, value: String) = "$property:$value;"
