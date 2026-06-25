package com.ssafy.jjongle.presentation.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import androidx.hilt.navigation.compose.hiltViewModel
import com.ssafy.jjongle.common.domain.helper.NavigationHelper
import com.ssafy.jjongle.common.domain.navigation.NavSignal
import com.ssafy.jjongle.common.presentation.jank.JankPageEffect
import com.ssafy.jjongle.main.domain.deeplink.matchRoute
import com.ssafy.jjongle.presentation.viewmodel.MusicViewModel
import com.ssafy.jjongle.presentation.viewmodel.NavigationViewModel

/**
 * Nav Graph는 메인에서 반복되는 계산이나 변환을 담당합니다.
 *
 * 호출부가 세부 구현을 직접 갖지 않도록 작은 공개 함수/값으로 분리합니다.
 */
@Composable
fun NavGraph(
    startDestination: String = AppRoutePaths.SPLASH,
    initialSyntheticStack: List<GenericNavKey> = listOf(GenericNavKey(startDestination)),
    navigationViewModel: NavigationViewModel,
    navigationHelper: NavigationHelper = NavigationHelper.NoOp,
) {
    val musicViewModel: MusicViewModel = hiltViewModel()
    val backStack = remember(initialSyntheticStack) {
        mutableStateListOf<GenericNavKey>().apply {
            addAll(initialSyntheticStack.ifEmpty { listOf(GenericNavKey(startDestination)) })
        }
    }
    val current = backStack.lastOrNull() ?: GenericNavKey(startDestination)
    val currentRoute = current.toComposeRoute()

    val navigator = remember(backStack) {
        object : AppRouteNavigator {
            override fun push(route: String) {
                backStack += route.toGenericNavKey()
            }

            override fun replaceAll(route: String) {
                backStack.replaceWith(listOf(route.toGenericNavKey()))
            }

            override fun pop() {
                if (backStack.size > 1) {
                    backStack.removeAt(backStack.lastIndex)
                }
            }

            override fun popTo(path: String, inclusive: Boolean) {
                val index = backStack.indexOfLast { it.path == path }
                if (index < 0) return
                val keepCount = if (inclusive) index else index + 1
                while (backStack.size > keepCount) {
                    backStack.removeAt(backStack.lastIndex)
                }
            }

            override fun navigateWithinStack(path: String, inclusive: Boolean, nextRoute: String) {
                popTo(path, inclusive)
                push(nextRoute)
            }
        }
    }

    BackHandler(enabled = backStack.size > 1) {
        navigator.pop()
    }

    LaunchedEffect(currentRoute) {
        if (current.path != AppRoutePaths.SPLASH) {
            navigationViewModel.saveRoute(currentRoute)
        }
    }

    LaunchedEffect(currentRoute) {
        musicViewModel.onRouteChanged(currentRoute)
    }

    LaunchedEffect(navigationHelper) {
        navigationHelper.navigationFlow.collect { signal ->
            when (signal) {
                is NavSignal.GoToDestPage -> handleNavRoute(backStack, signal.route.toGenericNavKey())
                is NavSignal.DeepLink -> handleDeepLink(backStack, signal.route.toGenericNavKey())
                NavSignal.Back -> navigator.pop()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .semantics { testTagsAsResourceId = true },
    ) {
        NavDisplay(
            backStack = backStack,
            onBack = { navigator.pop() },
            entryProvider = entryProvider {
                entry<GenericNavKey> { key ->
                    val route = appRouteByPath[key.path]
                    if (route == null) {
                        LaunchedEffect(key.path) {
                            backStack.replaceWith(listOf(GenericNavKey(AppRoutePaths.SPLASH)))
                        }
                    } else {
                        JankPageEffect(pageName = key.path)
                        route.render(key.args, navigator)
                    }
                }
            },
        )
    }
}

fun handleNavRoute(
    backStack: MutableList<GenericNavKey>,
    key: GenericNavKey,
) {
    val route = appRouteByPath[key.path] ?: return
    if (route.isBottomTab) {
        backStack.replaceWith(route.syntheticStack(key.args))
    } else {
        backStack += key
    }
}

fun handleDeepLink(
    backStack: MutableList<GenericNavKey>,
    key: GenericNavKey,
) {
    val route = appRouteByPath[key.path] ?: return
    if (route.isBottomTab) {
        handleNavRoute(backStack, key)
        return
    }
    backStack.replaceWith(backStack.toList().bringToFront(key))
}

private fun String.toGenericNavKey(): GenericNavKey {
    val route = substringBefore("?")
    val queryArgs = substringAfter("?", missingDelimiterValue = "")
        .takeIf { it.isNotBlank() }
        ?.split("&")
        ?.mapNotNull { part ->
            val key = part.substringBefore("=")
            val value = part.substringAfter("=", missingDelimiterValue = "")
            key.takeIf { it.isNotBlank() }?.let { it to value }
        }
        ?.toMap()
        .orEmpty()

    if (appRouteByPath.containsKey(route)) {
        return GenericNavKey(route, queryArgs)
    }

    val matchedRoute = matchRoute(appRoutePatterns, route)
    if (matchedRoute != null) {
        val (pattern, pathArgs) = matchedRoute
        return GenericNavKey(pattern.template, queryArgs + pathArgs)
    }

    return GenericNavKey(route, queryArgs)
}

private fun MutableList<GenericNavKey>.replaceWith(stack: List<GenericNavKey>) {
    clear()
    addAll(stack)
}
