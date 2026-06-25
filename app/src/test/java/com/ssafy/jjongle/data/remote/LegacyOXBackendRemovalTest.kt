package com.ssafy.jjongle.data.remote

import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.readText
import org.junit.Assert.assertFalse
import org.junit.Test

/**
 * Legacy OXBackend Removal Test 회귀를 막기 위한 테스트입니다.
 *
 * 리팩터링이나 모듈 이동 중에도 기존 동작, 아키텍처 경계, 오류 처리 계약이 유지되는지 확인합니다.
 */
class LegacyOXBackendRemovalTest {

    @Test
    fun legacy_ox_backend_api_and_websocket_stack_is_not_on_classpath() {
        listOf(
            "com.ssafy.jjongle.data.remote.OXGameApiService",
            "com.ssafy.jjongle.data.remote.OXGameRemoteDataSource",
            "com.ssafy.jjongle.data.remote.model.FinishOXGameRequest",
            "com.ssafy.jjongle.data.remote.model.oxgame.OXGameHistoriesPageDto",
            "com.ssafy.jjongle.data.remote.model.oxgame.OXGameHistoryDto",
            "com.ssafy.jjongle.data.remote.model.oxgame.OXGameWrongAnswerNoteDto",
            "com.ssafy.jjongle.data.websocket.GameWebSocketManager",
            "com.ssafy.jjongle.data.websocket.GameWebSocketEventParser",
            "com.ssafy.jjongle.data.model.GameFinishProfile",
            "com.ssafy.jjongle.data.model.GameFinishResponse",
            "com.ssafy.jjongle.data.model.GameFinishResultData",
            "com.ssafy.jjongle.data.model.GameStartData",
            "com.ssafy.jjongle.data.model.GameStartResponse",
            "com.ssafy.jjongle.data.model.PositionSubmitData",
            "com.ssafy.jjongle.data.model.QuizResponse",
            "com.ssafy.jjongle.data.model.SubmitResultData",
            "com.ssafy.jjongle.data.model.SubmitResultResponse",
            "com.ssafy.jjongle.data.model.UserPositionDto",
            "com.ssafy.jjongle.common.data.model.BaseRequest",
            "com.ssafy.jjongle.common.data.model.BaseResponse",
        ).forEach { className ->
            assertClassIsAbsent(className)
        }
    }

    @Test
    fun legacy_ox_websocket_build_config_is_removed() {
        val appBuild = repositoryRoot().resolve("app/build.gradle.kts").readText()

        assertFalse(appBuild.contains("WS_BASE_URL"))
        assertFalse(appBuild.contains("ws://"))
        assertFalse(appBuild.contains("/ws/group-game"))
    }

    private fun assertClassIsAbsent(className: String) {
        try {
            Class.forName(className)
            throw AssertionError("$className should have been removed")
        } catch (_: ClassNotFoundException) {
        }
    }

    private fun repositoryRoot(): Path =
        generateSequence(Path.of("").toAbsolutePath()) { it.parent }
            .first { Files.exists(it.resolve("settings.gradle.kts")) }
}
