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
fun FlowOrPhrasingContent.SvgIcon(
    width: String,
    height: String,
    viewBox: String,
    fill: String,
    fillRule: String,
    clipRule: String,
    d: String,
    pathFill: String
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
class SvgPath(consumer: TagConsumer<*>) : HTMLTag(
    "path", consumer, emptyMap(),
    inlineTag = true,
    emptyTag = false
), HtmlInlineTag {
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