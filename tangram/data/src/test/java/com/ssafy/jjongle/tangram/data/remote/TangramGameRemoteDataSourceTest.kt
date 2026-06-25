package com.ssafy.jjongle.tangram.data.remote

import com.ssafy.jjongle.common.domain.error.HttpResponseException
import com.ssafy.jjongle.common.domain.error.HttpResponseStatus
import com.ssafy.jjongle.tangram.data.remote.model.SingleGameDTO
import com.ssafy.jjongle.tangram.data.remote.model.TangramDetailDTO
import com.ssafy.jjongle.tangram.data.remote.model.TangramHistoriesPageDTO
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test
import retrofit2.Response

/**
 * Tangram Game Remote Data Source Test 회귀를 막기 위한 테스트입니다.
 *
 * 리팩터링이나 모듈 이동 중에도 기존 동작, 아키텍처 경계, 오류 처리 계약이 유지되는지 확인합니다.
 */
class TangramGameRemoteDataSourceTest {

    @Test
    fun get_single_game_returns_body_when_response_is_successful() = runTest {
        val body = SingleGameDTO(stage = 7)
        val dataSource = TangramGameRemoteDataSource(FakeApi(singleGameResponse = Response.success(body)))

        val result = dataSource.getSingleGame()

        assertSame(body, result)
    }

    @Test
    fun get_single_game_throws_http_response_exception_when_response_fails() = runTest {
        val dataSource = TangramGameRemoteDataSource(
            FakeApi(
                singleGameResponse = Response.error(
                    "{\"type\":\"api.tangram.sessionExpired\"}"
                        .toResponseBody("application/json".toMediaType()),
                    rawResponse(code = 401, message = "Unauthorized"),
                ),
            ),
        )

        val error = try {
            dataSource.getSingleGame()
            error("Expected HttpResponseException")
        } catch (e: HttpResponseException) {
            e
        }

        assertEquals(HttpResponseStatus.Unauthorized, error.status)
        assertEquals(401, error.rawCode)
        assertEquals("https://example.test/single-game", error.errorRequestUrl)
        assertEquals("{\"type\":\"api.tangram.sessionExpired\"}", error.cause?.message)
    }

    @Test
    fun get_tangram_histories_returns_body_when_response_is_successful() = runTest {
        val body = TangramHistoriesPageDTO(content = emptyList())
        val dataSource = TangramGameRemoteDataSource(
            FakeApi(historiesResponse = Response.success(body)),
        )

        val result = dataSource.getTangramHistories(page = 0, size = 20)

        assertSame(body, result)
    }

    @Test
    fun get_tangram_histories_throws_http_response_exception_when_response_fails() = runTest {
        val dataSource = TangramGameRemoteDataSource(
            FakeApi(
                historiesResponse = Response.error(
                    "{\"type\":\"api.tangram.historiesUnavailable\"}"
                        .toResponseBody("application/json".toMediaType()),
                    rawResponse(
                        url = "https://example.test/single-game/histories?page=0&size=20",
                        code = 503,
                        message = "Service Unavailable",
                    ),
                ),
            ),
        )

        val error = try {
            dataSource.getTangramHistories(page = 0, size = 20)
            error("Expected HttpResponseException")
        } catch (e: HttpResponseException) {
            e
        }

        assertEquals(HttpResponseStatus.Unknown, error.status)
        assertEquals(503, error.rawCode)
        assertEquals("https://example.test/single-game/histories?page=0&size=20", error.errorRequestUrl)
        assertEquals("{\"type\":\"api.tangram.historiesUnavailable\"}", error.cause?.message)
    }

    @Test
    fun get_tangram_detail_returns_body_when_response_is_successful() = runTest {
        val body = TangramDetailDTO(story = "story")
        val dataSource = TangramGameRemoteDataSource(
            FakeApi(detailResponse = Response.success(body)),
        )

        val result = dataSource.getTangramDetail(id = 42L)

        assertSame(body, result)
    }

    @Test
    fun get_tangram_detail_throws_http_response_exception_when_response_fails() = runTest {
        val dataSource = TangramGameRemoteDataSource(
            FakeApi(
                detailResponse = Response.error(
                    "{\"type\":\"api.tangram.detailUnavailable\"}"
                        .toResponseBody("application/json".toMediaType()),
                    rawResponse(
                        url = "https://example.test/single-game/history/42",
                        code = 404,
                        message = "Not Found",
                    ),
                ),
            ),
        )

        val error = try {
            dataSource.getTangramDetail(id = 42L)
            error("Expected HttpResponseException")
        } catch (e: HttpResponseException) {
            e
        }

        assertEquals(HttpResponseStatus.NotFound, error.status)
        assertEquals(404, error.rawCode)
        assertEquals("https://example.test/single-game/history/42", error.errorRequestUrl)
        assertEquals("{\"type\":\"api.tangram.detailUnavailable\"}", error.cause?.message)
    }

    private class FakeApi(
        private val singleGameResponse: Response<SingleGameDTO> = Response.success(SingleGameDTO()),
        private val historiesResponse: Response<TangramHistoriesPageDTO> = Response.success(TangramHistoriesPageDTO()),
        private val detailResponse: Response<TangramDetailDTO> = Response.success(TangramDetailDTO()),
    ) : TangramGameApiService {
        override suspend fun getTangramHistories(
            page: Int,
            size: Int,
        ): Response<TangramHistoriesPageDTO> = historiesResponse

        override suspend fun getTangramDetail(id: Long): Response<TangramDetailDTO> = detailResponse

        override suspend fun getSingleGame(): Response<SingleGameDTO> = singleGameResponse
    }

    private fun rawResponse(
        code: Int,
        message: String,
        url: String = "https://example.test/single-game",
    ): okhttp3.Response =
        okhttp3.Response.Builder()
            .request(Request.Builder().url(url).build())
            .protocol(Protocol.HTTP_1_1)
            .code(code)
            .message(message)
            .build()
}
