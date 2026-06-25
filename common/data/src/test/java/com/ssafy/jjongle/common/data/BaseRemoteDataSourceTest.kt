package com.ssafy.jjongle.common.data

import com.ssafy.jjongle.common.domain.error.HttpResponseException
import com.ssafy.jjongle.common.domain.error.HttpResponseStatus
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertThrows
import org.junit.Test
import retrofit2.Response

/**
 * Base Remote Data Source Test 회귀를 막기 위한 테스트입니다.
 *
 * 리팩터링이나 모듈 이동 중에도 기존 동작, 아키텍처 경계, 오류 처리 계약이 유지되는지 확인합니다.
 */
class BaseRemoteDataSourceTest {

    private val dataSource = TestRemoteDataSource()

    @Test
    fun check_response_returns_body_for_successful_response() {
        val body = SampleDto(name = "jjongle")

        val result = dataSource.unwrap(Response.success(body))

        assertSame(body, result)
    }

    @Test
    fun check_response_throws_http_response_exception_for_error_response() {
        val response = Response.error<SampleDto>(
            "{\"type\":\"api.sample.forceUpdate\"}".toResponseBody("application/json".toMediaType()),
            rawResponse(code = 401, message = "Unauthorized"),
        )

        val error = assertThrows(HttpResponseException::class.java) {
            dataSource.unwrap(response)
        }

        assertEquals(HttpResponseStatus.Unauthorized, error.status)
        assertEquals(401, error.rawCode)
        assertEquals("https://example.test/intro", error.errorRequestUrl)
        assertEquals("Unauthorized", error.message)
        assertEquals("{\"type\":\"api.sample.forceUpdate\"}", error.cause?.message)
    }

    @Test
    fun check_response_throws_http_response_exception_for_successful_null_body() {
        @Suppress("UNCHECKED_CAST")
        val response = Response.success(null) as Response<SampleDto>

        val error = assertThrows(HttpResponseException::class.java) {
            dataSource.unwrap(response)
        }

        assertEquals(HttpResponseStatus.Unknown, error.status)
        assertEquals(200, error.rawCode)
        assertEquals("Response body is null.", error.message)
    }

    private class TestRemoteDataSource : BaseRemoteDataSource() {
        fun unwrap(response: Response<SampleDto>): SampleDto = checkResponse(response)
    }

    private data class SampleDto(val name: String)

    private fun rawResponse(code: Int, message: String): okhttp3.Response =
        okhttp3.Response.Builder()
            .request(Request.Builder().url("https://example.test/intro").build())
            .protocol(Protocol.HTTP_1_1)
            .code(code)
            .message(message)
            .build()
}
