package com.ssafy.jjongle.presentation.architecture

import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.readText
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Main Presentation Module Extraction Contract Test 회귀를 막기 위한 테스트입니다.
 *
 * 리팩터링이나 모듈 이동 중에도 기존 동작, 아키텍처 경계, 오류 처리 계약이 유지되는지 확인합니다.
 */
class MainPresentationModuleExtractionContractTest {

    @Test
    fun main_presentation_implementation_does_not_live_in_app() {
        val root = repositoryRoot()
        val settings = String(Files.readAllBytes(root.resolve("settings.gradle.kts")))

        assertTrue(
            "settings.gradle.kts must include :main:presentation",
            settings.contains("include(\":main:presentation\")"),
        )

        val appPresentationRoot = root.resolve("app/src/main/java/com/ssafy/jjongle/presentation")
        val remainingAppPresentationFiles =
            if (Files.exists(appPresentationRoot)) {
                appPresentationRoot.toFile()
                    .walkTopDown()
                    .filter { it.isFile }
                    .map { appPresentationRoot.relativize(it.toPath()).toString() }
                    .toList()
            } else {
                emptyList()
            }

        assertTrue(
            "Presentation implementation must move out of :app into feature/main presentation modules: $remainingAppPresentationFiles",
            remainingAppPresentationFiles.isEmpty(),
        )

        assertTrue(
            ":app launcher Activity must remain app assembly code, not presentation package code",
            Files.exists(root.resolve("app/src/main/java/com/ssafy/jjongle/MainActivity.kt")),
        )

        assertFalse(
            ":app must not contain a data package",
            Files.exists(root.resolve("app/src/main/java/com/ssafy/jjongle/data")),
        )

        listOf(
            "navigation/NavGraph.kt",
            "ui/screen/MapScreen.kt",
            "viewmodel/AuthViewModel.kt",
            "state/ProfileState.kt",
        ).forEach { relativePath ->
            assertTrue(
                "Main presentation implementation must live in :main:presentation: $relativePath",
                Files.exists(
                    root.resolve(
                        "main/presentation/src/main/java/com/ssafy/jjongle/presentation/$relativePath"
                    )
                ),
            )
        }
    }

    @Test
    fun animal_drawable_mapping_stays_in_main_presentation_single_mapper() {
        val root = repositoryRoot()
        val mapper = root.resolve(
            "main/presentation/src/main/java/com/ssafy/jjongle/presentation/ui/mapper/AnimalTypeUi.kt",
        )
        val viewModel = root.resolve(
            "main/presentation/src/main/java/com/ssafy/jjongle/presentation/viewmodel/AnimalBookViewModel.kt",
        )
        val cameraScreen = root.resolve(
            "main/presentation/src/main/java/com/ssafy/jjongle/presentation/ui/screen/CameraScreen.kt",
        )
        val domainRoots = listOf(
            root.resolve("common/domain/src/main/kotlin"),
            root.resolve("main/domain/src/main/kotlin"),
            root.resolve("oxgame/domain/src/main/kotlin"),
            root.resolve("tangram/domain/src/main/kotlin"),
        )

        assertTrue("Animal drawable mapper must live in main presentation", Files.exists(mapper))

        val mapperSource = mapper.readText()
        assertTrue(mapperSource.contains("fun AnimalType.toImageRes(): Int"))
        assertTrue(mapperSource.contains("R.drawable.turtle"))
        assertFalse("Drawable resource mapper must not claim domain ownership", mapperSource.contains("도메인으로 옮겨라"))

        val viewModelSource = viewModel.readText()
        assertTrue(viewModelSource.contains("import com.ssafy.jjongle.presentation.ui.mapper.toImageRes"))
        assertFalse("ViewModel must not duplicate AnimalType drawable mapping", viewModelSource.contains("AnimalType.TURTLE -> R.drawable.turtle"))

        val cameraScreenSource = cameraScreen.readText()
        assertTrue(cameraScreenSource.contains("import com.ssafy.jjongle.presentation.ui.mapper.toImageRes"))
        assertTrue(cameraScreenSource.contains("?.toImageRes()"))
        assertFalse("CameraScreen must not duplicate string-to-drawable animal mapping", cameraScreenSource.contains("\"turtle\" -> R.drawable.turtle"))

        domainRoots
            .filter { Files.exists(it) }
            .flatMap { rootDir ->
                rootDir.toFile().walkTopDown()
                    .filter { it.isFile && it.extension == "kt" }
                    .map { it.toPath() }
                    .toList()
            }
            .forEach { sourceFile ->
                val source = sourceFile.readText()
                assertFalse(
                    "Domain must remain pure JVM and not reference Android drawables: ${root.relativize(sourceFile)}",
                    source.contains("R.drawable"),
                )
            }
    }

    private fun repositoryRoot(): Path =
        generateSequence(Path.of("").toAbsolutePath()) { it.parent }
            .first { Files.exists(it.resolve("settings.gradle.kts")) }
}
