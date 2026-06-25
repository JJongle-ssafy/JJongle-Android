package com.ssafy.jjongle.common.domain.base

import com.ssafy.jjongle.common.domain.error.HttpResponseException
import com.ssafy.jjongle.common.domain.error.HttpResponseStatus
import com.ssafy.jjongle.common.domain.helper.MessageHelper
import com.ssafy.jjongle.common.domain.helper.NavigationHelper
import com.ssafy.jjongle.common.domain.helper.ResourceHelper
import com.ssafy.jjongle.tti.TTIHelper
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Base Use Case Test 회귀를 막기 위한 테스트입니다.
 *
 * 리팩터링이나 모듈 이동 중에도 기존 동작, 아키텍처 경계, 오류 처리 계약이 유지되는지 확인합니다.
 */
class BaseUseCaseTest {

    @Test
    fun execute_common_error_handling_shows_session_expired_for_unauthorized() {
        val messageHelper = RecordingMessageHelper()
        val useCase = TestUseCase(messageHelper)

        useCase.handle(exception(401))

        assertEquals("세션이 만료되었습니다.", messageHelper.oneButtonDialogDesc)
    }

    @Test
    fun execute_common_error_handling_shows_unsupported_feature_for_not_found() {
        val messageHelper = RecordingMessageHelper()
        val useCase = TestUseCase(messageHelper)

        useCase.handle(exception(404))

        assertEquals("지원하지 않는 기능입니다.", messageHelper.oneButtonDialogDesc)
    }

    @Test
    fun execute_common_error_handling_shows_retry_message_for_server_errors() {
        val messageHelper = RecordingMessageHelper()
        val useCase = TestUseCase(messageHelper)

        useCase.handle(exception(503))

        assertEquals("잠시 후 다시 시도해주세요.", messageHelper.oneButtonDialogDesc)
    }

    @Test
    fun handle_common_http_failure_executes_common_handling_for_failure_result() {
        val messageHelper = RecordingMessageHelper()
        val useCase = TestUseCase(messageHelper)
        val exception = exception(401)
        val failure = Result.failure<String>(exception)

        val result = useCase.handleResult(failure)

        assertTrue(result.isFailure)
        assertSame(exception, result.exceptionOrNull())
        assertEquals("세션이 만료되었습니다.", messageHelper.oneButtonDialogDesc)
    }

    @Test
    fun execute_with_common_http_handling_wraps_success_value_in_result() = runTest {
        val useCase = TestUseCase(MessageHelper.NoOp)

        val result = useCase.execute { "ok" }

        assertTrue(result.isSuccess)
        assertEquals("ok", result.getOrThrow())
    }

    @Test
    fun execute_with_common_http_handling_handles_common_http_error_and_returns_failure() = runTest {
        val messageHelper = RecordingMessageHelper()
        val useCase = TestUseCase(messageHelper)
        val exception = exception(503)

        val result = useCase.execute<String> { throw exception }

        assertTrue(result.isFailure)
        assertSame(exception, result.exceptionOrNull())
        assertEquals("잠시 후 다시 시도해주세요.", messageHelper.oneButtonDialogDesc)
    }

    @Test
    fun execute_common_error_handling_ignores_non_common_http_error() {
        val messageHelper = RecordingMessageHelper()
        val useCase = TestUseCase(messageHelper)

        useCase.handle(exception(400))

        assertEquals(null, messageHelper.oneButtonDialogDesc)
    }

    @Test
    fun execute_with_common_http_handling_propagates_non_common_http_error_without_dialog() = runTest {
        val messageHelper = RecordingMessageHelper()
        val useCase = TestUseCase(messageHelper)
        val exception = exception(400)

        val result = useCase.execute<String> { throw exception }

        assertTrue(result.isFailure)
        assertSame(exception, result.exceptionOrNull())
        assertEquals(null, messageHelper.oneButtonDialogDesc)
    }

    private class TestUseCase(
        messageHelper: MessageHelper,
    ) : BaseUseCase(
        resourceHelper = object : ResourceHelper {
            override fun getString(id: Int): String = id.toString()
        },
        messageHelper = messageHelper,
        navigationHelper = NavigationHelper.NoOp,
        ttiHelper = TTIHelper.NoOp,
    ) {
        fun handle(error: HttpResponseException) {
            executeCommonErrorHanding(error)
        }

        fun <T> handleResult(result: Result<T>): Result<T> {
            return result.handleCommonHttpFailure()
        }

        suspend fun <T> execute(block: suspend () -> T): Result<T> {
            return executeWithCommonHttpHandling(block)
        }
    }

    private class RecordingMessageHelper : MessageHelper {
        var oneButtonDialogDesc: String? = null

        override fun showToast(messageText: String) = Unit

        override fun showSnackBar(messageText: String) = Unit

        override fun showOneButtonDialog(
            cantIgnore: Boolean,
            descText: String,
            onClickButton: () -> Unit,
        ) {
            oneButtonDialogDesc = descText
        }

        override fun showTwoButtonDialog(
            descText: String,
            onClickPositive: () -> Unit,
            onClickNegative: () -> Unit,
        ) = Unit
    }

    private fun exception(rawCode: Int): HttpResponseException = HttpResponseException(
        status = HttpResponseStatus.entries.firstOrNull { it.code == rawCode } ?: HttpResponseStatus.Unknown,
        rawCode = rawCode,
        errorRequestUrl = "https://example.test/common",
        msg = "failed",
    )
}
