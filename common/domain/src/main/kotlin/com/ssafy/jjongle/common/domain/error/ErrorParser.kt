package com.ssafy.jjongle.common.domain.error

/**
 * Http Response Exception은 인증 흐름에서 발생한 domain 오류를 예외로 전달합니다.
 *
 * 화면과 UseCase가 Firebase/HTTP 세부 예외 대신 앱에서 정의한 오류 타입을 처리하도록 합니다.
 */
fun HttpResponseException.isCommonErrorHandling(): Boolean =
    rawCode == 401 || rawCode == 404 || rawCode >= 500

inline fun <reified ErrorType> HttpResponseException.handlingErrorOnUseCase(): ErrorType?
    where ErrorType : Enum<ErrorType>, ErrorType : HttpErrorType {
    val serverType = cause?.message ?: return null
    return enumValues<ErrorType>()
        .firstOrNull { it.type == serverType && it.isHandledOnDomain }
}
