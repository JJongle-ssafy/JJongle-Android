package com.ssafy.jjongle.tti

/**
 * 화면 진입부터 최초 상호작용 가능 시점까지의 TTI 이벤트를 기록하는 계약입니다.
 *
 * 화면별 start/end 마커와 메타데이터를 같은 형식으로 남겨 성능 회귀를 비교할 수 있게 합니다.
 */
interface TTIHelper {
    /**
     * 특정 페이지의 TTI 측정을 시작합니다.
     */
    fun startTTITracking(page: TTIPage)

    /**
     * TTI를 구성하는 세부 구간 측정을 시작합니다.
     */
    fun startTTITimeline(page: TTIPage, timelineCategory: TTITimelineCategory)

    /**
     * TTI 세부 구간 측정을 종료합니다.
     */
    fun endTTITimeline(page: TTIPage, timelineCategory: TTITimelineCategory)

    /**
     * 최초 의미 있는 화면 표시가 완료되었음을 기록합니다.
     */
    fun endTTITracking(page: TTIPage)

    /**
     * 누적된 TTI 정보를 로그 또는 원격 채널로 발사합니다.
     */
    fun shotTTILogging(page: TTIPage)

    /**
     * TTI 로그에 추가 메타데이터를 기록합니다.
     */
    fun addTTIMetaData(page: TTIPage, metadata: TTIMetadata, value: String)

    /**
     * 테스트나 preview 환경에서 TTI 기록을 무시하기 위한 빈 구현체입니다.
     */
    object NoOp : TTIHelper {
        override fun startTTITracking(page: TTIPage) = Unit

        override fun startTTITimeline(page: TTIPage, timelineCategory: TTITimelineCategory) = Unit

        override fun endTTITimeline(page: TTIPage, timelineCategory: TTITimelineCategory) = Unit

        override fun endTTITracking(page: TTIPage) = Unit

        override fun shotTTILogging(page: TTIPage) = Unit

        override fun addTTIMetaData(page: TTIPage, metadata: TTIMetadata, value: String) = Unit
    }
}
