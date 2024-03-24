package dev.shreyaspatil.composeCompilerMetricsGenerator.generator.content.common

import dev.shreyaspatil.composeCompilerMetricsGenerator.generator.style.Colors
import kotlinx.html.FlowOrPhrasingContent

fun FlowOrPhrasingContent.cross(width: String, height: String, fill: String = Colors.RED_DARK) {
    SvgIcon(
        width = width,
        height = height,
        viewBox = "0 0 24 24",
        fill = "none",
        fillRule = "evenodd",
        clipRule = "evenodd",
        d = "M11 13C11 13.5523 11.4477 14 12 14C12.5523 14 13 13.5523 13 13V10C13 9.44772 12.5523 9 12 9C11.4477 9 11 9.44772 11 10V13ZM13 15.9888C13 15.4365 12.5523 14.9888 12 14.9888C11.4477 14.9888 11 15.4365 11 15.9888V16C11 16.5523 11.4477 17 12 17C12.5523 17 13 16.5523 13 16V15.9888ZM9.37735 4.66136C10.5204 2.60393 13.4793 2.60393 14.6223 4.66136L21.2233 16.5431C22.3341 18.5427 20.8882 21 18.6008 21H5.39885C3.11139 21 1.66549 18.5427 2.77637 16.5431L9.37735 4.66136Z",
        pathFill = fill
    )
}

fun FlowOrPhrasingContent.check(width: String, height: String, fill: String = Colors.GREEN_DARK) {
    SvgIcon(
        width = width,
        height = height,
        viewBox = "0 0 24 24",
        fill = "none",
        fillRule = "evenodd",
        clipRule = "evenodd",
        d = "M22 12C22 17.5228 17.5228 22 12 22C6.47715 22 2 17.5228 2 12C2 6.47715 6.47715 2 12 2C17.5228 2 22 6.47715 22 12ZM16.0303 8.96967C16.3232 9.26256 16.3232 9.73744 16.0303 10.0303L11.0303 15.0303C10.7374 15.3232 10.2626 15.3232 9.96967 15.0303L7.96967 13.0303C7.67678 12.7374 7.67678 12.2626 7.96967 11.9697C8.26256 11.6768 8.73744 11.6768 9.03033 11.9697L10.5 13.4393L12.7348 11.2045L14.9697 8.96967C15.2626 8.67678 15.7374 8.67678 16.0303 8.96967Z",
        pathFill = fill
    )
}