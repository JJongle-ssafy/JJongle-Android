package com.ssafy.jjongle.common.domain.error

/**
 * HttpResponseStatus 모듈 기능을 표현하는 class 선언입니다.
 *
 * - 계층: common/domain
 * - 책임: 소속 계층의 역할을 타입으로 분리해 호출 경계를 명확히 합니다.
 */
enum class HttpResponseStatus(val code: Int, val msg: String) {
    Unauthorized(401, "unauthorized"),
    NotFound(404, "not_found"),
    ServerError(500, "server_error"),
    Unknown(-1, "unknown"),
}

/**
 * HttpErrorType 모듈 기능을 표현하는 interface 선언입니다.
 *
 * - 계층: common/domain
 * - 책임: 소속 계층의 역할을 타입으로 분리해 호출 경계를 명확히 합니다.
 */
interface HttpErrorType {
    /**
     * 서버 에러 응답의 식별 문자열입니다.
     */
    val type: String

    /**
     * 사용자에게 노출할 수 있는 기본 에러 메시지입니다.
     */
    val errorMsg: String

    /**
     * true이면 UseCase에서 처리하고, false이면 presentation에서 화면 상태와 함께 처리합니다.
     */
    val isHandledOnDomain: Boolean
}

/**
 * HttpResponseException 모듈 기능을 표현하는 class 선언입니다.
 *
 * - 계층: common/domain
 * - 책임: 소속 계층의 역할을 타입으로 분리해 호출 경계를 명확히 합니다.
 */
class HttpResponseException(
    val status: HttpResponseStatus,
    val rawCode: Int,
    val errorRequestUrl: String,
    msg: String? = null,
    cause: Throwable? = null,
) : Exception(msg, cause)
