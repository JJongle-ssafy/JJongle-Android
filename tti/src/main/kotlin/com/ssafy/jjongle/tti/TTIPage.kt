package com.ssafy.jjongle.tti

/**
 * TTIPage 화면을 타입 안전하게 표현하는 라우팅 값입니다.
 *
 * 문자열 경로와 인자를 객체 안에 모아 Navigation host와 기능 모듈이 같은 이동 계약을 공유하게 합니다.
 */
interface TTIPage {
    val pageName: String
}
