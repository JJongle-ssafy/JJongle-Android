package com.ssafy.jjongle.common.data.mapping

import java.time.LocalDateTime

/**
 * MISSING_SERVER_ID는 공통에서 반복되는 계산이나 변환을 담당합니다.
 *
 * 호출부가 세부 구현을 직접 갖지 않도록 작은 공개 함수/값으로 분리합니다.
 */
const val MISSING_SERVER_ID = -1
const val MISSING_SERVER_LONG_ID = -1L
const val MISSING_SERVER_COORDINATE = 0.0

fun String?.orMissingServerField(fieldName: String): String =
    this?.takeIf { it.isNotBlank() } ?: "[MISSING_SERVER_FIELD:$fieldName]"

fun Int?.orMissingServerId(): Int = this ?: MISSING_SERVER_ID

fun Long?.orMissingServerLongId(): Long = this ?: MISSING_SERVER_LONG_ID

fun Double?.orMissingServerCoordinate(): Double = this ?: MISSING_SERVER_COORDINATE

fun missingServerDateTime(): LocalDateTime = LocalDateTime.of(1970, 1, 1, 0, 0)
