package com.ssafy.jjongle.tti

/**
 * TTITimeline Category는 TTI 흐름에서 허용되는 상태나 이벤트 종류를 제한합니다.
 *
 * 문자열이나 숫자 상수 대신 타입 분기로 처리해 잘못된 값이 전달되는 일을 줄입니다.
 */
enum class TTITimelineCategory {
    API_REQUEST_READY_TIME,
    API_RESPONSE_TIME,
    VIEW_CREATION,
    VIEW_BINDING,
    IMAGE_LOADED,
}

/**
 * TTIMetadata는 TTI 흐름에서 허용되는 상태나 이벤트 종류를 제한합니다.
 *
 * 문자열이나 숫자 상수 대신 타입 분기로 처리해 잘못된 값이 전달되는 일을 줄입니다.
 */
enum class TTIMetadata {
    PAGE_NAME,
    IS_BOUNCED,
    IS_TIMEOUT,
    TTI_LOG_VERSION,
}
