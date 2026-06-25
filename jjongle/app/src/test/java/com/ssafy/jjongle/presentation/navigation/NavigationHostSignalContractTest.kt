package com.ssafy.jjongle.presentation.navigation

import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.readText
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * NavigationHostSignal의 아키텍처 또는 동작 계약을 검증하는 테스트입니다.
 *
 * - 계층: test
 * - 책임: 회귀를 막는 대표 시나리오와 모듈 경계 조건을 실행합니다.
 */
class NavigationHostSignalContractTest {

    @Test
    fun nav_graph_collects_navigation_helper_signals() {
        val navGraph = repositoryRoot()
            .resolve("main/presentation/src/main/java/com/ssafy/jjongle/presentation/navigation/NavGraph.kt")
            .readText()

        assertTrue(navGraph.contains("navigationHelper: NavigationHelper"))
        assertTrue(navGraph.contains("navigationHelper.navigationFlow.collect"))
        assertTrue(navGraph.contains("NavSignal.GoToDestPage"))
        assertTrue(navGraph.contains("NavSignal.DeepLink"))
        assertTrue(navGraph.contains("NavSignal.Back"))
        assertTrue(navGraph.contains("handleNavRoute(backStack, signal.route.toGenericNavKey())"))
        assertTrue(navGraph.contains("handleDeepLink(backStack, signal.route.toGenericNavKey())"))
        assertTrue(navGraph.contains("NavSignal.Back -> navigator.pop()"))
        assertTrue(navGraph.contains("fun handleNavRoute("))
        assertTrue(navGraph.contains("fun handleDeepLink("))
        assertTrue(navGraph.contains("backStack.replaceWith(backStack.toList().bringToFront(key))"))
        assertTrue(navGraph.contains("mutableStateListOf<GenericNavKey>()"))
        assertTrue(navGraph.contains("NavDisplay("))
        assertTrue(navGraph.contains("entryProvider"))
        assertTrue(navGraph.contains("entry<GenericNavKey>"))
        assertTrue(navGraph.contains("route.render(key.args, navigator)"))
        assertTrue(!navGraph.contains("NavHostController"))
        assertTrue(!navGraph.contains("NavHost("))
        assertTrue(!navGraph.contains("androidx.navigation.compose"))
    }

    @Test
    fun main_activity_injects_navigation_helper_into_nav_graph() {
        val mainActivity = repositoryRoot()
            .resolve("app/src/main/java/com/ssafy/jjongle/MainActivity.kt")
            .readText()

        assertTrue(mainActivity.contains("lateinit var navigationHelper: NavigationHelper"))
        assertTrue(mainActivity.contains("navigationHelper = navigationHelper"))
    }

    private fun repositoryRoot(): Path =
        generateSequence(Path.of("").toAbsolutePath()) { it.parent }
            .first { Files.exists(it.resolve("settings.gradle.kts")) }
}
