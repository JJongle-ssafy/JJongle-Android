package com.ssafy.jjongle.data.model

import java.time.LocalDateTime

internal const val MISSING_SERVER_ID = -1
internal const val MISSING_SERVER_LONG_ID = -1L
internal const val MISSING_SERVER_COORDINATE = 0.0

internal fun String?.orMissingServerField(fieldName: String): String =
    this?.takeIf { it.isNotBlank() } ?: "[MISSING_SERVER_FIELD:$fieldName]"

internal fun Int?.orMissingServerId(): Int = this ?: MISSING_SERVER_ID

internal fun Long?.orMissingServerLongId(): Long = this ?: MISSING_SERVER_LONG_ID

internal fun Double?.orMissingServerCoordinate(): Double = this ?: MISSING_SERVER_COORDINATE

internal fun missingServerDateTime(): LocalDateTime = LocalDateTime.of(1970, 1, 1, 0, 0)
