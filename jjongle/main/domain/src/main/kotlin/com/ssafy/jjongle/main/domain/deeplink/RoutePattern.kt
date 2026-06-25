package com.ssafy.jjongle.main.domain.deeplink

/**
 * RoutePattern 모듈 기능을 표현하는 class 선언입니다.
 *
 * - 계층: main/domain
 * - 책임: 소속 계층의 역할을 타입으로 분리해 호출 경계를 명확히 합니다.
 */
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
