package com.ssafy.jjongle.common.entity

/**
 * 공통 기반 흐름에서 허용되는 Bgm Group 값의 집합입니다.
 *
 * 분기 가능한 상태나 이벤트를 타입으로 제한해 잘못된 문자열/숫자 값이 계층 사이로 전달되지 않게 합니다.
 */
enum class BgmGroup {
    WORLD,
    TANGRAM,
    OX,
    MYPAGE
}
