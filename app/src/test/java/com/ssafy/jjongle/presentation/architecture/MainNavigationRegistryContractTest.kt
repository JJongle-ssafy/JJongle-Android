package com.ssafy.jjongle.presentation.architecture

import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.readText
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * MainNavigationRegistry의 아키텍처 또는 동작 계약을 검증하는 테스트입니다.
 *
 * - 계층: test
 * - 책임: 회귀를 막는 대표 시나리오와 모듈 경계 조건을 실행합니다.
 */
class MainNavigationRegistryContractTest {

    @Test
    fun main_navigation_uses_route_registry_instead_of_screen_sealed_class() {
        val root = repositoryRoot()
        val navigationRoot =
            root.resolve("main/presentation/src/main/java/com/ssafy/jjongle/presentation/navigation")

        assertFalse(
            "Legacy Screen sealed class must be removed; AppRouteRegistry is the single route source",
            Files.exists(navigationRoot.resolve("Screen.kt")),
        )

        listOf("AppRoute.kt", "AppRouteRegistry.kt", "GenericNavKey.kt").forEach { fileName ->
            assertTrue(
                "Navigation3-shaped route contract file is missing: $fileName",
                Files.exists(navigationRoot.resolve(fileName)),
            )
        }

        val registry = navigationRoot.resolve("AppRouteRegistry.kt").readText()
        listOf(
            "SPLASH",
            "SIGNUP",
            "LOGIN",
            "MAP",
            "MY_PAGE",
            "QUIZ_NOTE",
            "ANIMAL_BOOK",
            "SETTING",
            "OX_GAME",
            "OX_GAME_TITLE",
            "OX_GAME_INTRO",
            "OX_GAME_TUTORIAL",
            "TANGRAM_TITLE",
            "TANGRAM_INTRO",
            "TANGRAM_STAGE",
            "BEFORE_TANGRAM_TUTORIAL",
            "TANGRAM_TUTORIAL",
            "CAMERA",
        ).forEach { routeConstant ->
            assertTrue("AppRoutePaths must define $routeConstant", registry.contains("const val $routeConstant"))
        }
        assertTrue("AppRouteRegistry must expose appRoutes", registry.contains("val appRoutes"))
        assertTrue("AppRouteRegistry must expose appRouteByPath", registry.contains("val appRouteByPath"))
        assertTrue("AppRoute must render with navigator actions", navigationRoot.resolve("AppRoute.kt").readText().contains("AppRouteNavigator"))
        listOf(
            "SplashScreen(",
            "LoginScreen(",
            "SignupScreen(",
            "MapScreen(",
            "MypageScreen(",
            "QuizNoteScreen(",
            "AnimalBookScreen(",
            "SettingScreen(",
            "OXGameTitleScreen(",
            "OXGameScreen(",
            "TangramTitleScreen(",
            "TangramStageScreen(",
            "CameraScreen(",
        ).forEach { renderTarget ->
            assertTrue("AppRouteRegistry must own render target $renderTarget", registry.contains(renderTarget))
        }

        val navGraph = navigationRoot.resolve("NavGraph.kt").readText()
        val bgmRouting = navigationRoot.resolve("BgmRouting.kt").readText()
        val mainActivity = root.resolve("app/src/main/java/com/ssafy/jjongle/MainActivity.kt").readText()
        val mainPresentationGradle = root.resolve("main/presentation/build.gradle.kts").readText()

        listOf(navGraph, bgmRouting, mainActivity).forEach { source ->
            assertFalse("Navigation code must not depend on Screen.* routes", source.contains("Screen."))
        }
        assertFalse("Navigation3-style host must not use Nav2 NavHostController", navGraph.contains("NavHostController"))
        assertFalse("Navigation3-style host must not use Nav2 NavHost", navGraph.contains("NavHost("))
        assertFalse("MainActivity must not create Nav2 rememberNavController", mainActivity.contains("rememberNavController"))
        assertTrue("Navigation host must render from GenericNavKey back stack", navGraph.contains("mutableStateListOf<GenericNavKey>()"))
        assertTrue("Navigation host must use official Navigation3 NavDisplay", navGraph.contains("NavDisplay("))
        assertTrue("Navigation host must use official Navigation3 typed entry", navGraph.contains("entry<GenericNavKey>"))
        assertTrue("Navigation host must dispatch render through registry", navGraph.contains("route.render(key.args, navigator)"))
        assertFalse("Navigation host must not own route screen switch", navGraph.contains("when (current.path)"))
        assertFalse("Navigation host must not import screen composables directly", navGraph.contains(".presentation.ui.screen."))
        assertTrue("Navigation host must expose route policy helpers", navGraph.contains("fun handleNavRoute("))
        assertTrue("Navigation host must expose deeplink policy helpers", navGraph.contains("fun handleDeepLink("))
        assertTrue("BGM routing must use route registry metadata", bgmRouting.contains("appRoutes"))
        assertTrue("main:presentation must depend on Navigation3 runtime", mainPresentationGradle.contains("libs.navigation3.runtime"))
        assertTrue("main:presentation must depend on Navigation3 UI", mainPresentationGradle.contains("libs.navigation3.ui"))
        assertFalse("main:presentation must not directly depend on Nav2 navigation-compose", mainPresentationGradle.contains("libs.navigation.compose"))

        val genericNavKey = navigationRoot.resolve("GenericNavKey.kt").readText()
        assertTrue("GenericNavKey must implement Navigation3 NavKey", genericNavKey.contains(": NavKey"))
        assertTrue("GenericNavKey must be serializable for Navigation3 state restoration", genericNavKey.contains("@Serializable"))
    }

    @Test
    fun production_sources_do_not_reintroduce_navigation2_api() {
        val root = repositoryRoot()
        val sourceRoots = listOf(
            root.resolve("app/src/main/java"),
            root.resolve("common"),
            root.resolve("main"),
            root.resolve("oxgame"),
            root.resolve("tangram"),
        )
        val forbiddenTokens = listOf(
            "androidx.navigation.compose",
            "NavHostController",
            "rememberNavController",
            "NavHost(",
            "composable(",
            "navArgument(",
        )

        val offenders = sourceRoots
            .filter(Files::exists)
            .flatMap { sourceRoot ->
                Files.walk(sourceRoot).use { paths ->
                    paths
                        .filter { Files.isRegularFile(it) }
                        .filter { it.toString().endsWith(".kt") || it.toString().endsWith(".kts") }
                        .flatMap { file ->
                            val source = file.readText()
                            forbiddenTokens
                                .filter(source::contains)
                                .map { token -> "${root.relativize(file)} contains $token" }
                                .stream()
                        }
                        .toList()
                }
            }

        assertTrue(
            "Production sources must stay on Navigation3/AppRouteRegistry APIs, but found: $offenders",
            offenders.isEmpty(),
        )
    }

    private fun repositoryRoot(): Path =
        generateSequence(Path.of("").toAbsolutePath()) { it.parent }
            .first { Files.exists(it.resolve("settings.gradle.kts")) }
}
