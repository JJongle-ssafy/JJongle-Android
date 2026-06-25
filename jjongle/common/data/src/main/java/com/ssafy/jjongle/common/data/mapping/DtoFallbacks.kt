package com.ssafy.jjongle.common.data.mapping

import java.time.LocalDateTime

/**
 * MISSING_SERVER_ID에서 공유하는 최상위 값을 제공합니다.
 *
 * - 계층: common/data
 * - 책임: 모듈 내부에서 반복 사용되는 설정이나 상태 기준을 한곳에 둡니다.
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
