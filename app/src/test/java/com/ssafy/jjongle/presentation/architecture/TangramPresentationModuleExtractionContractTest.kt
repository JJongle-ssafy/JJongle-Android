package com.ssafy.jjongle.presentation.architecture

import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.extension
import kotlin.io.path.isRegularFile
import kotlin.io.path.readText
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Tangram Presentation Module Extraction Contract Test 회귀를 막기 위한 테스트입니다.
 *
 * 리팩터링이나 모듈 이동 중에도 기존 동작, 아키텍처 경계, 오류 처리 계약이 유지되는지 확인합니다.
 */
class TangramPresentationModuleExtractionContractTest {

    @Test
    fun tangram_screens_and_viewmodel_live_in_feature_presentation_module() {
        val root = repositoryRoot()
        val appPresentationRoot = root.resolve("app/src/main/java/com/ssafy/jjongle/presentation")

        listOf(
            "state/TangramGameState.kt",
            "ui/screen/BeforeTangramTutorialScreen.kt",
            "ui/screen/TangramStageScreen.kt",
            "ui/screen/TangramTitleScreen.kt",
            "ui/screen/TangramTutorialScreen.kt",
            "viewmodel/TangramStageViewModel.kt",
        ).forEach { relativePath ->
            assertFalse(
                "Tangram presentation implementation must not remain in :app: $relativePath",
                Files.exists(appPresentationRoot.resolve(relativePath)),
            )
        }

        val featurePresentationRoot =
            root.resolve("tangram/presentation/src/main/java/com/ssafy/jjongle/tangram/presentation")

        listOf(
            "state/TangramGameState.kt",
            "ui/screen/BeforeTangramTutorialScreen.kt",
            "ui/screen/TangramStageScreen.kt",
            "ui/screen/TangramTitleScreen.kt",
            "ui/screen/TangramTutorialScreen.kt",
            "viewmodel/TangramStageViewModel.kt",
        ).forEach { relativePath ->
            assertTrue(
                "Tangram presentation implementation must live in :tangram:presentation: $relativePath",
                Files.exists(featurePresentationRoot.resolve(relativePath)),
            )
        }
    }

    @Test
    fun tangram_presentation_sources_use_feature_owned_package_namespace() {
        val root = repositoryRoot()
        val sourceRoot = root.resolve("tangram/presentation/src/main/java")
        val sourceFiles = Files.walk(sourceRoot).use { paths ->
            paths
                .filter { it.isRegularFile() && it.extension == "kt" }
                .toList()
        }

        assertTrue("Tangram presentation module must contain Kotlin sources", sourceFiles.isNotEmpty())

        sourceFiles.forEach { sourceFile ->
            val source = sourceFile.readText()
            assertTrue(
                "$sourceFile must declare a tangram-owned presentation package",
                Regex("""^package com\.ssafy\.jjongle\.tangram\.presentation(?:\.|\n)""", RegexOption.MULTILINE)
                    .containsMatchIn(source),
            )
            assertFalse(
                "$sourceFile must not keep the shared presentation package namespace",
                Regex("""^package com\.ssafy\.jjongle\.presentation(?:\.|\n)""", RegexOption.MULTILINE)
                    .containsMatchIn(source),
            )
        }
    }

    @Test
    fun main_route_registry_imports_tangram_screens_from_feature_package() {
        val root = repositoryRoot()
        val registry = root
            .resolve("main/presentation/src/main/java/com/ssafy/jjongle/presentation/navigation/AppRouteRegistry.kt")
            .readText()

        listOf(
            "BeforeTangramTutorialScreen",
            "TangramStageScreen",
            "TangramTitleScreen",
            "TangramTutorialScreen",
        ).forEach { screenName ->
            assertTrue(
                "AppRouteRegistry must import $screenName from tangram presentation",
                registry.contains("import com.ssafy.jjongle.tangram.presentation.ui.screen.$screenName"),
            )
            assertFalse(
                "AppRouteRegistry must not import $screenName from shared presentation namespace",
                registry.contains("import com.ssafy.jjongle.presentation.ui.screen.$screenName"),
            )
        }
    }

    private fun repositoryRoot(): Path =
        generateSequence(Path.of("").toAbsolutePath()) { it.parent }
            .first { Files.exists(it.resolve("settings.gradle.kts")) }
}
