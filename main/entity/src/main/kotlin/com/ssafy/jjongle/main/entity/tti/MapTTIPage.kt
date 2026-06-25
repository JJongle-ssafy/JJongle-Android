package com.ssafy.jjongle.main.entity.tti

import com.ssafy.jjongle.tti.TTIPage

/**
 * MapTTIPage 앱 내부에서 공유하는 도메인 값을 표현합니다.
 *
 * - 계층: main/entity
 * - 책임: 불변 값과 도메인 의미를 계층 사이에 전달합니다.
 */
object MapTTIPage : TTIPage {
    override val pageName: String = "map"
}
