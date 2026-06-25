package com.ssafy.jjongle.data.architecture

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * OXGame Repository Error Boundary Contract Test 회귀를 막기 위한 테스트입니다.
 *
 * 리팩터링이나 모듈 이동 중에도 기존 동작, 아키텍처 경계, 오류 처리 계약이 유지되는지 확인합니다.
 */
class OXGameRepositoryErrorBoundaryContractTest {

    @Test
    fun ox_game_start_and_submit_failures_are_handled_at_usecase_boundary_not_data_repository() {
        val repositoryContract = source(
            "oxgame/domain/src/main/kotlin/com/ssafy/jjongle/oxgame/domain/repository/OXGameRepository.kt"
        )
        val repositoryImpl = source(
            "oxgame/data/src/main/java/com/ssafy/jjongle/oxgame/data/repository/OXGameRepositoryImpl.kt"
        )
        val startUseCase = source(
            "oxgame/domain/src/main/kotlin/com/ssafy/jjongle/oxgame/domain/usecase/StartOXGameUseCase.kt"
        )
        val gameActionUseCase = source(
            "oxgame/domain/src/main/kotlin/com/ssafy/jjongle/oxgame/domain/usecase/GameActionUseCase.kt"
        )

        assertTrue(repositoryContract.contains("suspend fun startGameSession()"))
        assertTrue(repositoryContract.contains("suspend fun finishGameSession("))
        assertFalse(repositoryContract.contains("finishOXGame"))
        assertFalse(repositoryContract.contains("Legacy 이름 유지"))
        assertTrue(repositoryContract.contains("suspend fun sendSubmitAnswer("))
        assertFalse(repositoryImpl.contains("runCatching"))
        assertFalse(repositoryImpl.contains("catch ("))
        assertTrue(startUseCase.contains("suspend fun startGameSession(): Result<Unit>"))
        assertTrue(gameActionUseCase.contains("suspend fun sendSubmitAnswer("))
        assertTrue(gameActionUseCase.contains("): Result<Unit>"))
    }

    private fun source(path: String): String = projectRoot().resolve(path).readText()

    private fun projectRoot(): File {
        var current = File(".").absoluteFile
        while (current.parentFile != null) {
            if (current.resolve("settings.gradle.kts").exists()) return current
            current = current.parentFile!!
        }
        error("Project root not found")
    }
}
