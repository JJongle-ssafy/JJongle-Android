package com.ssafy.jjongle.presentation.architecture

import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.readText
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Mvi Architecture Contract Test 회귀를 막기 위한 테스트입니다.
 *
 * 리팩터링이나 모듈 이동 중에도 기존 동작, 아키텍처 경계, 오류 처리 계약이 유지되는지 확인합니다.
 */
class MviArchitectureContractTest {

    @Test
    fun ui_state_collection_fields_use_immutable_collections() {
        val root = repositoryRoot()
        val stateSources = Files.walk(root)
            .filter { path ->
                val normalized = path.toString()
                Files.isRegularFile(path) &&
                    normalized.endsWith(".kt") &&
                    "/src/main/" in normalized &&
                    (
                        normalized.endsWith("State.kt") ||
                            normalized.endsWith("UiState.kt") ||
                            normalized.endsWith("UIState.kt")
                        )
            }
            .toList()

        assertTrue("Expected production state files", stateSources.isNotEmpty())

        stateSources.forEach { path ->
            val source = path.readText()
            Regex("""val\s+\w+\s*:\s*(List|Set)<""").findAll(source).forEach { match ->
                val relative = root.relativize(path)
                error("$relative uses ${match.value}; UI state collections must use ImmutableList/ImmutableSet")
            }
            val relative = root.relativize(path)
            assertFalse("$relative must not use emptyList() in UI state", source.contains("emptyList()"))
            assertFalse("$relative must not use emptySet() in UI state", source.contains("emptySet()"))
            assertFalse("$relative must not use setOf() in UI state", source.contains("setOf("))
        }
    }

    @Test
    fun tangram_stage_state_uses_immutable_set_slots() {
        val root = repositoryRoot()
        val stageViewModel = root.resolve(
            "tangram/presentation/src/main/java/com/ssafy/jjongle/tangram/presentation/viewmodel/TangramStageViewModel.kt",
        ).readText()
        val gameState = root.resolve(
            "tangram/presentation/src/main/java/com/ssafy/jjongle/tangram/presentation/state/TangramGameState.kt",
        ).readText()

        assertTrue(stageViewModel.contains("val unlockedStages: ImmutableSet<Int>"))
        assertTrue(stageViewModel.contains("val completedStages: ImmutableSet<Int>"))
        assertTrue(gameState.contains("val movementPath: ImmutableList<Int>"))
        assertTrue(gameState.contains("val unlockedStages: ImmutableSet<Int>"))
        assertTrue(gameState.contains("val completedStages: ImmutableSet<Int>"))
    }

    @Test
    fun ui_states_expose_companion_empty_and_view_models_use_it_as_initial_state() {
        val root = repositoryRoot()
        val uiStateFiles = Files.walk(root)
            .filter { path ->
                val normalized = path.toString()
                Files.isRegularFile(path) &&
                    normalized.endsWith(".kt") &&
                    "/src/main/" in normalized &&
                    path.readText().contains(": UiState")
            }
            .toList()

        assertTrue("Expected production UiState files", uiStateFiles.isNotEmpty())

        uiStateFiles.forEach { path ->
            val relative = root.relativize(path)
            val source = path.readText()
            Regex("""\)\s*:\s*UiState""").findAll(source).forEach { uiStateMarker ->
                val stateName = Regex("""data class\s+(\w+)\s*\(""")
                    .findAll(source.substring(0, uiStateMarker.range.first))
                    .lastOrNull()
                    ?.groupValues
                    ?.get(1)
                    ?: error("$relative has UiState marker without data class declaration")
                assertTrue(
                    "$relative $stateName must expose companion empty",
                    source.contains(Regex("""companion object\s*\{[\s\S]*?val empty = $stateName\(""")),
                )
            }
        }

        val viewModelSources = Files.walk(root)
            .filter { path ->
                val normalized = path.toString()
                Files.isRegularFile(path) &&
                    normalized.endsWith("ViewModel.kt") &&
                    "/src/main/" in normalized &&
                    path.readText().contains("MviViewModel<")
            }
            .toList()

        viewModelSources.forEach { path ->
            val source = path.readText()
            Regex("""MviViewModel<[^,]+,\s*(\w+),\s*[^>]+>\((\w+)\(\)\)""")
                .find(source)
                ?.let { match ->
                    val relative = root.relativize(path)
                    error("$relative must use ${match.groupValues[1]}.empty as initial state")
                }
            Regex("""initialState\s*=\s*(\w+)\(\)""")
                .find(source)
                ?.let { match ->
                    val relative = root.relativize(path)
                    error("$relative must use ${match.groupValues[1]}.empty as initial state")
                }
        }
    }

    private fun repositoryRoot(): Path =
        generateSequence(Path.of("").toAbsolutePath()) { it.parent }
            .first { Files.exists(it.resolve("settings.gradle.kts")) }
}
