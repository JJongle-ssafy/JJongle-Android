package com.ssafy.jjongle.common.domain.error

/**
 * 공통 기반 흐름에서 허용되는 Http Response Status 값의 집합입니다.
 *
 * 분기 가능한 상태나 이벤트를 타입으로 제한해 잘못된 문자열/숫자 값이 계층 사이로 전달되지 않게 합니다.
 */
enum class HttpResponseStatus(val code: Int, val msg: String) {
    Unauthorized(401, "unauthorized"),
    NotFound(404, "not_found"),
    ServerError(500, "server_error"),
    Unknown(-1, "unknown"),
}

/**
 * Http Error Type는 공통 흐름에서 사용하는 타입입니다.
 *
 * 호출부가 구현 세부보다 역할이 드러나는 타입에 의존하도록 분리합니다.
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
 * Http Response Exception은 인증 흐름에서 발생한 domain 오류를 예외로 전달합니다.
 *
 * 화면과 UseCase가 Firebase/HTTP 세부 예외 대신 앱에서 정의한 오류 타입을 처리하도록 합니다.
 */
class HttpResponseException(
    val status: HttpResponseStatus,
    val rawCode: Int,
    val errorRequestUrl: String,
    msg: String? = null,
    cause: Throwable? = null,
) : Exception(msg, cause)
