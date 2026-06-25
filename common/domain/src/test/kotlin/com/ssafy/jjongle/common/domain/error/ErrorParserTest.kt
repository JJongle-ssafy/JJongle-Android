package com.ssafy.jjongle.common.domain.error

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * ErrorParser의 아키텍처 또는 동작 계약을 검증하는 테스트입니다.
 *
 * - 계층: test
 * - 책임: 회귀를 막는 대표 시나리오와 모듈 경계 조건을 실행합니다.
 */
class ErrorParserTest {

    private enum class SampleError(
        override val type: String,
        override val errorMsg: String,
        override val isHandledOnDomain: Boolean,
    ) : HttpErrorType {
        ForceUpdate("api.sample.forceUpdate", "force update", true),
        PresentationOnly("api.sample.presentationOnly", "presentation only", false),
    }

    @Test
    fun common_error_handling_matches_auth_not_found_and_server_errors() {
        assertTrue(exception(rawCode = 401).isCommonErrorHandling())
        assertTrue(exception(rawCode = 404).isCommonErrorHandling())
        assertTrue(exception(rawCode = 500).isCommonErrorHandling())
        assertTrue(exception(rawCode = 503).isCommonErrorHandling())
        assertFalse(exception(rawCode = 400).isCommonErrorHandling())
    }

    @Test
    fun handling_error_on_use_case_returns_domain_handled_feature_error() {
        val result = exception(serverType = "api.sample.forceUpdate")
            .handlingErrorOnUseCase<SampleError>()

        assertEquals(SampleError.ForceUpdate, result)
    }

    @Test
    fun handling_error_on_use_case_ignores_presentation_only_errors() {
        val result = exception(serverType = "api.sample.presentationOnly")
            .handlingErrorOnUseCase<SampleError>()

        assertNull(result)
    }

    private fun exception(
        rawCode: Int = 400,
        serverType: String? = null,
    ): HttpResponseException = HttpResponseException(
        status = HttpResponseStatus.Unknown,
        rawCode = rawCode,
        errorRequestUrl = "https://example.test/sample",
        msg = "failed",
        cause = serverType?.let(::Throwable),
    )
}
