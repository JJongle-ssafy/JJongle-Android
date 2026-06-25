package com.ssafy.jjongle.presentation.architecture

import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.extension
import kotlin.io.path.isRegularFile
import kotlin.io.path.name
import kotlin.io.path.readText
import org.junit.Assert.assertFalse
import org.junit.Test

/**
 * TtsLegacyRemoval의 아키텍처 또는 동작 계약을 검증하는 테스트입니다.
 *
 * - 계층: test
 * - 책임: 회귀를 막는 대표 시나리오와 모듈 경계 조건을 실행합니다.
 */
class TtsLegacyRemovalContractTest {

    @Test
    fun tts_legacy_runtime_is_not_present_in_production_sources() {
        val root = repositoryRoot()
        val forbiddenNames = setOf(
            "TTSUseCase.kt",
            "TTSState.kt",
            "TtsAudio.kt",
            "AudioPlayer.kt",
            "OXGameAudioPlayer.kt",
            "TangramAudioPlayer.kt",
            "IntroViewModel.kt",
            "TutorialViewModel.kt",
            "OXTutorialViewModel.kt",
            "TangramTutorialViewModel.kt",
        )

        productionKotlinFiles(root).forEach { file ->
            assertFalse(
                "TTS legacy file must be removed from production sources: ${root.relativize(file)}",
                file.name in forbiddenNames,
            )

            val source = file.readText()
            listOf(
                "TTSUseCase",
                "TTSState",
                "TtsAudio",
                "generateTTS",
                "playTTS",
                "ResetTtsState",
                "ttsState",
                "generateQuestionTTS",
                "generateExplanationTTS",
                "resetTTSState",
            ).forEach { token ->
                assertFalse(
                    "Production source must not reference TTS legacy token '$token': ${root.relativize(file)}",
                    source.contains(token),
                )
            }
        }
    }

    private fun productionKotlinFiles(root: Path): Sequence<Path> =
        Files.walk(root)
            .iterator()
            .asSequence()
            .filter { path ->
                path.isRegularFile() &&
                    path.extension == "kt" &&
                    path.toString().contains("/src/main/") &&
                    !path.toString().contains("/build/")
            }

    private fun repositoryRoot(): Path =
        generateSequence(Path.of("").toAbsolutePath()) { it.parent }
            .first { Files.exists(it.resolve("settings.gradle.kts")) }
}
