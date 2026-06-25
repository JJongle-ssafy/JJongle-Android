package com.ssafy.jjongle.presentation.deeplink

import android.net.Uri
import com.ssafy.jjongle.common.domain.navigation.NavRoute
import com.ssafy.jjongle.main.domain.deeplink.RoutePattern
import com.ssafy.jjongle.main.domain.deeplink.matchRoute

/**
 * Uri는 메인에서 반복되는 계산이나 변환을 담당합니다.
 *
 * 호출부가 세부 구현을 직접 갖지 않도록 작은 공개 함수/값으로 분리합니다.
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
