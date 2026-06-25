package com.ssafy.jjongle.oxgame.domain.usecase

import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.readText
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * GameActionUseCaseErrorHandling의 아키텍처 또는 동작 계약을 검증하는 테스트입니다.
 *
 * - 계층: test
 * - 책임: 회귀를 막는 대표 시나리오와 모듈 경계 조건을 실행합니다.
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
