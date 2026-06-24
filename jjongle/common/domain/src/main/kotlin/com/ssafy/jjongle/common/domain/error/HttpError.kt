package com.ssafy.jjongle.common.domain.error

enum class HttpResponseStatus(val code: Int, val msg: String) {
    Unauthorized(401, "unauthorized"),
    NotFound(404, "not_found"),
    ServerError(500, "server_error"),
    Unknown(-1, "unknown"),
}

interface HttpErrorType {
    val type: String
    val errorMsg: String
    val isHandledOnDomain: Boolean
}

class HttpResponseException(
    val status: HttpResponseStatus,
    val rawCode: Int,
    val errorRequestUrl: String,
    msg: String? = null,
    cause: Throwable? = null,
) : Exception(msg, cause)
