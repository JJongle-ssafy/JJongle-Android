package com.ssafy.jjongle.main.domain.deeplink

data class RoutePattern(
    val template: String,
) {
    private val templateSegments = template.toSegments()

    fun match(path: String): Map<String, String>? {
        val pathSegments = path.toSegments()
        if (templateSegments.size != pathSegments.size) return null

        val args = linkedMapOf<String, String>()
        templateSegments.zip(pathSegments).forEach { (templatePart, pathPart) ->
            if (templatePart.isPlaceholder()) {
                args[templatePart.removeSurrounding("{", "}")] = pathPart
            } else if (templatePart != pathPart) {
                return null
            }
        }
        return args
    }
}

fun matchRoute(
    patterns: List<RoutePattern>,
    path: String,
): Pair<RoutePattern, Map<String, String>>? =
    patterns.firstNotNullOfOrNull { pattern ->
        pattern.match(path)?.let { args -> pattern to args }
    }

private fun String.toSegments(): List<String> =
    trim('/').split('/').filter { it.isNotBlank() }

private fun String.isPlaceholder(): Boolean =
    startsWith("{") && endsWith("}") && length > 2
