package com.ssafy.jjongle.tti

interface TTIHelper {
    fun startTTITracking(page: TTIPage)

    fun startTTITimeline(page: TTIPage, timelineCategory: TTITimelineCategory)

    fun endTTITimeline(page: TTIPage, timelineCategory: TTITimelineCategory)

    fun endTTITracking(page: TTIPage)

    fun shotTTILogging(page: TTIPage)

    fun addTTIMetaData(page: TTIPage, metadata: TTIMetadata, value: String)

    object NoOp : TTIHelper {
        override fun startTTITracking(page: TTIPage) = Unit

        override fun startTTITimeline(page: TTIPage, timelineCategory: TTITimelineCategory) = Unit

        override fun endTTITimeline(page: TTIPage, timelineCategory: TTITimelineCategory) = Unit

        override fun endTTITracking(page: TTIPage) = Unit

        override fun shotTTILogging(page: TTIPage) = Unit

        override fun addTTIMetaData(page: TTIPage, metadata: TTIMetadata, value: String) = Unit
    }
}
