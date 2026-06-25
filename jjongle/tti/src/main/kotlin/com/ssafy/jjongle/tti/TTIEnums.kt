package com.ssafy.jjongle.tti

/**
 * TTITimelineCategory TTI 계측에 필요한 값을 정의합니다.
 *
 * - 계층: tti
 * - 책임: 화면 진입부터 최초 상호작용 가능 시점까지의 성능 측정을 보조합니다.
 */
enum class TTITimelineCategory {
    API_REQUEST_READY_TIME,
    API_RESPONSE_TIME,
    VIEW_CREATION,
    VIEW_BINDING,
    IMAGE_LOADED,
}

/**
 * TTIMetadata TTI 계측에 필요한 값을 정의합니다.
 *
 * - 계층: tti
 * - 책임: 화면 진입부터 최초 상호작용 가능 시점까지의 성능 측정을 보조합니다.
 */
enum class TTIMetadata {
    PAGE_NAME,
    IS_BOUNCED,
    IS_TIMEOUT,
    TTI_LOG_VERSION,
}
