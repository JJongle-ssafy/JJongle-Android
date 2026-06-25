package com.ssafy.jjongle.presentation.navigation

import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.readText
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Navigation Host Signal Contract Test 회귀를 막기 위한 테스트입니다.
 *
 * 리팩터링이나 모듈 이동 중에도 기존 동작, 아키텍처 경계, 오류 처리 계약이 유지되는지 확인합니다.
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
