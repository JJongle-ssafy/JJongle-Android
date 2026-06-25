package com.ssafy.jjongle.common.domain.error

/**
 * HttpResponseException 기능에서 사용하는 최상위 헬퍼입니다.
 *
 * - 계층: common/domain
 * - 책임: 파일의 대표 작업을 함수 단위로 분리해 호출 지점을 단순하게 유지합니다.
 */
fun HttpResponseException.isCommonErrorHandling(): Boolean =
    rawCode == 401 || rawCode == 404 || rawCode >= 500

inline fun <reified ErrorType> HttpResponseException.handlingErrorOnUseCase(): ErrorType?
    where ErrorType : Enum<ErrorType>, ErrorType : HttpErrorType {
    val serverType = cause?.message ?: return null
    return enumValues<ErrorType>()
        .firstOrNull { it.type == serverType && it.isHandledOnDomain }
}
