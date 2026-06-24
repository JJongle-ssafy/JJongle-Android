package com.ssafy.jjongle.common.domain.error

fun HttpResponseException.isCommonErrorHandling(): Boolean =
    rawCode == 401 || rawCode == 404 || rawCode >= 500

inline fun <reified ErrorType> HttpResponseException.handlingErrorOnUseCase(): ErrorType?
    where ErrorType : Enum<ErrorType>, ErrorType : HttpErrorType {
    val serverType = cause?.message ?: return null
    return enumValues<ErrorType>()
        .firstOrNull { it.type == serverType && it.isHandledOnDomain }
}
