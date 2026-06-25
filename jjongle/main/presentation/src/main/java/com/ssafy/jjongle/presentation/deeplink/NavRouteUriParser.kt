package com.ssafy.jjongle.presentation.deeplink

import android.net.Uri
import com.ssafy.jjongle.common.domain.navigation.NavRoute
import com.ssafy.jjongle.main.domain.deeplink.RoutePattern
import com.ssafy.jjongle.main.domain.deeplink.matchRoute

/**
 * Uri 기능에서 사용하는 최상위 헬퍼입니다.
 *
 * - 계층: main/presentation
 * - 책임: 파일의 대표 작업을 함수 단위로 분리해 호출 지점을 단순하게 유지합니다.
 */
fun Uri.resolveRoute(
    registeredPaths: Set<String>,
    routePatterns: List<RoutePattern>,
): NavRoute? {
    val normalizedPath = path.orEmpty().ifBlank { "/" }
    val queryArgs = queryParameterNames.associateWith { key ->
        getQueryParameter(key).orEmpty()
    }

    if (normalizedPath in registeredPaths) {
        return NavRoute(path = normalizedPath, args = queryArgs)
    }

    val matched = matchRoute(routePatterns, normalizedPath) ?: return null
    return NavRoute(
        path = matched.first.template,
        args = queryArgs + matched.second,
    )
}
