package com.ssafy.jjongle.common.domain.usecase

import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.readText
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * QueryUseCaseErrorHandling의 아키텍처 또는 동작 계약을 검증하는 테스트입니다.
 *
 * - 계층: test
 * - 책임: 회귀를 막는 대표 시나리오와 모듈 경계 조건을 실행합니다.
 */
class QueryUseCaseErrorHandlingContractTest {

    @Test
    fun query_use_cases_use_base_use_case_common_result_wrapper() {
        val cases = listOf(
            Case(
                relativePath = "oxgame/domain/src/main/kotlin/com/ssafy/jjongle/oxgame/domain/usecase/GetOXGameHistoriesUseCase.kt",
                wrapperCall = "executeWithCommonHttpHandling { repo.getHistories(page) }",
            ),
            Case(
                relativePath = "oxgame/domain/src/main/kotlin/com/ssafy/jjongle/oxgame/domain/usecase/GetOXGameHistoryDetailUseCase.kt",
                wrapperCall = "executeWithCommonHttpHandling { repo.getHistoryDetail(historyId) }",
            ),
            Case(
                relativePath = "tangram/domain/src/main/kotlin/com/ssafy/jjongle/tangram/domain/usecase/GetTangramHistoriesUseCase.kt",
                wrapperCall = "executeWithCommonHttpHandling { repo.getTangramHistories(page, size) }",
            ),
            Case(
                relativePath = "tangram/domain/src/main/kotlin/com/ssafy/jjongle/tangram/domain/usecase/GetTangramDetailUseCase.kt",
                wrapperCall = "executeWithCommonHttpHandling { repo.getTangramDetail(id, type) }",
            ),
            Case(
                relativePath = "tangram/domain/src/main/kotlin/com/ssafy/jjongle/tangram/domain/usecase/TangramGameUseCase.kt",
                wrapperCall = "executeWithCommonHttpHandling { tangramGameRepository.getCurrentChallengeStageId() }",
            ),
        )

        cases.forEach { case ->
            val source = sourcePath(case.relativePath).readText()

            assertTrue(
                "${case.relativePath} should route repository query through BaseUseCase common wrapper",
                source.contains(case.wrapperCall),
            )
            assertFalse(
                "${case.relativePath} should not duplicate try/catch Result wrapping",
                Regex("""(?:=|return)\s*try\s*\{""").containsMatchIn(source),
            )
            assertFalse(
                "${case.relativePath} should not call common error handling directly for simple query delegation",
                source.contains("executeCommonErrorHanding"),
            )
        }
    }

    private data class Case(
        val relativePath: String,
        val wrapperCall: String,
    )

    private fun sourcePath(relativePath: String): Path {
        val root = generateSequence(Path.of("").toAbsolutePath()) { it.parent }
            .first { Files.exists(it.resolve("settings.gradle.kts")) }
        return root.resolve(relativePath)
    }
}
