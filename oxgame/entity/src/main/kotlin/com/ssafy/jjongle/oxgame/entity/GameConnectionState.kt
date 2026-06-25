package com.ssafy.jjongle.oxgame.entity

/**
 * OX 게임 화면을 렌더링하는 데 필요한 값을 담는 상태 스냅샷입니다.
 *
 * 여러 값을 화면에서 따로 수집하지 않도록 한 모델로 묶어, 상태 변경 지점을 ViewModel 안에서 추적할 수 있게 합니다.
 */
enum class GameConnectionState {
    DISCONNECTED,
    CONNECTING,
    CONNECTED,
    DISCONNECTING,
    ERROR
}
