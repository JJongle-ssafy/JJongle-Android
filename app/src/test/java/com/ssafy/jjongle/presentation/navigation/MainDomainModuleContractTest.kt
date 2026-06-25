package com.ssafy.jjongle.presentation.navigation

import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.readText
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * MainDomainModule의 아키텍처 또는 동작 계약을 검증하는 테스트입니다.
 *
 * - 계층: test
 * - 책임: 회귀를 막는 대표 시나리오와 모듈 경계 조건을 실행합니다.
 */
class MainDomainModuleContractTest {

    @Test
    fun main_domain_owns_pure_route_pattern_matching() {
        val root = repositoryRoot()
        val settings = root.resolve("settings.gradle.kts").readText()
        val buildFile = root.resolve("main/domain/build.gradle.kts").readText()
        val routePattern = root.resolve(
            "main/domain/src/main/kotlin/com/ssafy/jjongle/main/domain/deeplink/RoutePattern.kt",
        ).readText()

        assertTrue(settings.contains("""include(":main:domain")"""))
        assertTrue(buildFile.contains("libs.plugins.kotlin.jvm"))
        assertTrue(routePattern.contains("data class RoutePattern"))
        assertTrue(routePattern.contains("fun matchRoute"))
    }

    @Test
    fun feature_domain_modules_own_navigation_page_objects() {
        val root = repositoryRoot()
        val mainPages = root.resolve(
            "main/domain/src/main/kotlin/com/ssafy/jjongle/main/domain/navigation/MainPages.kt",
        ).readText()
        val oxPages = root.resolve(
            "oxgame/domain/src/main/kotlin/com/ssafy/jjongle/oxgame/domain/navigation/OXGamePages.kt",
        ).readText()
        val tangramPages = root.resolve(
            "tangram/domain/src/main/kotlin/com/ssafy/jjongle/tangram/domain/navigation/TangramPages.kt",
        ).readText()
        val registry = root.resolve(
            "main/presentation/src/main/java/com/ssafy/jjongle/presentation/navigation/AppRouteRegistry.kt",
        ).readText()

        listOf(mainPages, oxPages, tangramPages).forEach { source ->
            assertTrue(source.contains("import com.ssafy.jjongle.common.domain.navigation.Page"))
            assertTrue(source.contains("override fun toRoute(): NavRoute"))
            assertTrue(source.contains(": Page"))
        }

        assertTrue(mainPages.contains("object MapPage : Page"))
        assertTrue(mainPages.contains("data class CameraPage("))
        assertTrue(oxPages.contains("object OXGameTitlePage : Page"))
        assertTrue(tangramPages.contains("object TangramStagePage : Page"))

        assertTrue(registry.contains("const val MAP = MapPage.PATH"))
        assertTrue(registry.contains("const val OX_GAME_TITLE = OXGameTitlePage.PATH"))
        assertTrue(registry.contains("const val TANGRAM_STAGE = TangramStagePage.PATH"))
        assertTrue(registry.contains("const val CAMERA = CameraPage.PATH"))
        assertTrue(registry.contains("fun camera(animal: String): String = CameraPage.routePath(animal)"))
    }

    @Test
    fun main_activity_uses_start_destination_for_cold_deeplink_and_signal_for_warm_deeplink() {
        val mainActivity = repositoryRoot()
            .resolve("app/src/main/java/com/ssafy/jjongle/MainActivity.kt")
            .readText()

        assertTrue(mainActivity.contains("resolveDeepLinkStartStack()"))
        assertTrue(mainActivity.contains("?: listOf(GenericNavKey(AppRoutePaths.SPLASH))"))
        assertTrue(mainActivity.contains("startDestination = coldStartStack.first().toComposeRoute()"))
        assertTrue(mainActivity.contains("initialSyntheticStack = coldStartStack"))
        assertTrue(mainActivity.contains("handleWarmDeepLink(intent)"))
        assertTrue(mainActivity.contains("navigationHelper.navigateDeepLink(route)"))
        assertTrue(!mainActivity.contains("navigateByRoute(route)"))
    }

    private fun repositoryRoot(): Path =
        generateSequence(Path.of("").toAbsolutePath()) { it.parent }
            .first { Files.exists(it.resolve("settings.gradle.kts")) }
}
