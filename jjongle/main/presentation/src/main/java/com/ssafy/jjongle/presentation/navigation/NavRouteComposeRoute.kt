package com.ssafy.jjongle.presentation.navigation

import com.ssafy.jjongle.common.domain.navigation.NavRoute

fun NavRoute.toGenericNavKey(): GenericNavKey =
    GenericNavKey(path = path, args = args)

fun GenericNavKey.toComposeRoute(): String =
    NavRoute(path = path, args = args).toComposeRoute()

fun NavRoute.toComposeRoute(): String {
    val consumedArgs = mutableSetOf<String>()
    val routeWithPathArgs = args.entries.fold(path) { route, (key, value) ->
        val placeholder = "{$key}"
        if (route.contains(placeholder)) {
            consumedArgs += key
            route.replace(placeholder, value)
        } else {
            route
        }
    }
    val query = args
        .filterKeys { it !in consumedArgs }
        .entries
        .joinToString("&") { (key, value) -> "$key=$value" }

    return if (query.isBlank()) routeWithPathArgs else "$routeWithPathArgs?$query"
}

fun NavRoute.toSyntheticComposeStack(): List<String> {
    return toSyntheticNavStack().map { it.toComposeRoute() }
}

fun NavRoute.toSyntheticNavStack(): List<GenericNavKey> {
    val route = appRouteByPath[path] ?: return listOf(toGenericNavKey())
    return route.syntheticStack(args)
}
