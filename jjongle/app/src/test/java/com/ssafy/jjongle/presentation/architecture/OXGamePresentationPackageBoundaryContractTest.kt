package com.ssafy.jjongle.presentation.architecture

import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.extension
import kotlin.io.path.isRegularFile
import kotlin.io.path.readText
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class OXGamePresentationPackageBoundaryContractTest {

    @Test
    fun ox_game_presentation_sources_use_feature_owned_package_namespace() {
        val root = repositoryRoot()
        val sourceRoot = root.resolve("oxgame/presentation/src/main/java")
        val sourceFiles = Files.walk(sourceRoot).use { paths ->
            paths
                .filter { it.isRegularFile() && it.extension == "kt" }
                .toList()
        }

        assertTrue("OXGame presentation module must contain Kotlin sources", sourceFiles.isNotEmpty())

        sourceFiles.forEach { sourceFile ->
            val source = sourceFile.readText()
            assertTrue(
                "$sourceFile must declare an oxgame-owned presentation package",
                Regex("""^package com\.ssafy\.jjongle\.oxgame\.presentation(?:\.|\n)""", RegexOption.MULTILINE)
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
    fun main_route_registry_imports_ox_game_screens_from_feature_package() {
        val root = repositoryRoot()
        val registry = root
            .resolve("main/presentation/src/main/java/com/ssafy/jjongle/presentation/navigation/AppRouteRegistry.kt")
            .readText()

        listOf(
            "OXGameScreen",
            "OXGameTitleScreen",
            "OXTutorialScreen",
        ).forEach { screenName ->
            assertTrue(
                "AppRouteRegistry must import $screenName from oxgame presentation",
                registry.contains("import com.ssafy.jjongle.oxgame.presentation.ui.screen.$screenName"),
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
