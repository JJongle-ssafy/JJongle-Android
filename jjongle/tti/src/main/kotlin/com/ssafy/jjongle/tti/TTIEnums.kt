package com.ssafy.jjongle.tti

enum class TTITimelineCategory {
    API_REQUEST_READY_TIME,
    API_RESPONSE_TIME,
    VIEW_CREATION,
    VIEW_BINDING,
    IMAGE_LOADED,
}

enum class TTIMetadata {
    PAGE_NAME,
    IS_BOUNCED,
    IS_TIMEOUT,
    TTI_LOG_VERSION,
}
