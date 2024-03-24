package dev.shreyaspatil.composeCompilerMetricsGenerator.generator.content.common

import dev.shreyaspatil.composeCompilerMetricsGenerator.generator.style.setStyle
import kotlinx.html.FlowOrPhrasingContent
import kotlinx.html.SPAN
import kotlinx.html.span

fun FlowOrPhrasingContent.IconWithText(
    icon: SPAN.() -> Unit,
    text: String,
    fontSize: String = "18px"
) {
    span {
        setStyle(alignItems = "center", display = "flex", fontSize = fontSize)

        icon()
        +text
    }
}

fun FlowOrPhrasingContent.CrossIconWithText(
    text: String,
    fontSize: String = "18px"
) {
    IconWithText(
        icon = {
            cross(width = fontSize, height = fontSize)
        },
        text = text,
        fontSize = fontSize
    )
}

fun FlowOrPhrasingContent.CheckIconWithText(
    text: String,
    fontSize: String = "18px"
) {
    IconWithText(
        icon = {
            check(width = fontSize, height = fontSize)
        },
        text = text,
        fontSize = fontSize
    )
}

