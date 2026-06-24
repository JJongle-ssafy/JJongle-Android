package com.ssafy.jjongle.common.data

import com.ssafy.jjongle.common.domain.error.HttpResponseException
import com.ssafy.jjongle.common.domain.error.HttpResponseStatus
import retrofit2.Response

abstract class BaseRemoteDataSource {
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
