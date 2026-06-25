package com.ssafy.jjongle.oxgame.domain.usecase

import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.readText
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Game Action Use Case Error Handling Contract Test 회귀를 막기 위한 테스트입니다.
 *
 * 리팩터링이나 모듈 이동 중에도 기존 동작, 아키텍처 경계, 오류 처리 계약이 유지되는지 확인합니다.
 */
class GameActionUseCaseErrorHandlingContractTest {

    @Test
    fun report_game_finish_uses_base_use_case_common_result_wrapper() {
        val source = sourcePath(
            "oxgame/domain/src/main/kotlin/com/ssafy/jjongle/oxgame/domain/usecase/GameActionUseCase.kt"
        ).readText()

        assertTrue(
            "reportGameFinish should route finishGameSession through BaseUseCase common wrapper",
            source.contains("executeWithCommonHttpHandling { oxGameRepository.finishGameSession(sessionKey) }"),
        )
        assertFalse(
            "GameActionUseCase should not duplicate try/catch Result wrapping",
            Regex("""=\s*try\s*\{""").containsMatchIn(source),
        )
        assertFalse(
            "GameActionUseCase should not call common error handling directly for simple repository delegation",
            source.contains("executeCommonErrorHanding"),
        )
    }

    private fun sourcePath(relativePath: String): Path {
        val root = generateSequence(Path.of("").toAbsolutePath()) { it.parent }
            .first { Files.exists(it.resolve("settings.gradle.kts")) }
        return root.resolve(relativePath)
    }
}
