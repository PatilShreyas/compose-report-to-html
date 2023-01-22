package dev.shreyaspatil.composeCompilerMetricsGenerator.core.utils

private val REGEX_CAMEL_CASE = "(\\A[a-z]|[A-Z])".toRegex()
fun camelCaseToWord(content: String): String =
    content.replace(REGEX_CAMEL_CASE) { " ${it.value[0].uppercase()}" }.trim()