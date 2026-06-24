package com.ssafy.jjongle.presentation.deeplink

import android.net.Uri
import com.ssafy.jjongle.common.domain.navigation.NavRoute
import com.ssafy.jjongle.main.domain.deeplink.RoutePattern
import com.ssafy.jjongle.main.domain.deeplink.matchRoute

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
