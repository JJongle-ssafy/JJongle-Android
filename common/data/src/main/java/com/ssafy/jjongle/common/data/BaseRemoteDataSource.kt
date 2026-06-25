package com.ssafy.jjongle.common.data

import com.ssafy.jjongle.common.domain.error.HttpResponseException
import com.ssafy.jjongle.common.domain.error.HttpResponseStatus
import retrofit2.Response

/**
 * Base 데이터를 외부 서비스나 로컬 저장소에서 읽고 쓰는 data 계층 경계입니다.
 *
 * Repository가 세부 API, SDK, 저장 방식에 직접 묶이지 않도록 데이터 접근 작업을 캡슐화합니다.
 */
abstract class BaseRemoteDataSource {
    /**
     * Retrofit [Response]를 성공 body 또는 [HttpResponseException]으로 변환합니다.
     *
     * 성공 응답이어도 body가 없으면 실패로 취급합니다.
     */
    protected fun <T : Any> checkResponse(response: Response<T>): T {
        if (response.isSuccessful) {
            return response.body() ?: throw HttpResponseException(
                status = HttpResponseStatus.Unknown,
                rawCode = response.code(),
                errorRequestUrl = response.raw()?.request?.url?.toString().orEmpty(),
                msg = "Response body is null.",
            )
        }

        throw HttpResponseException(
            status = HttpResponseStatus.entries.firstOrNull { it.code == response.code() }
                ?: HttpResponseStatus.Unknown,
            rawCode = response.code(),
            errorRequestUrl = response.raw().request.url.toString(),
            msg = response.message(),
            cause = Throwable(response.errorBody()?.string()),
        )
    }
}
