package com.ssafy.jjongle.common.presentation.mvi

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * 화면에서 ViewModel로 들어오는 사용자 입력이나 화면 생명주기 이벤트를 표현하는 MVI 입력 계약입니다.
 *
 * 각 화면은 이 계약을 구현한 Intent를 통해 상태 변경의 시작점을 명확히 남깁니다.
 */
interface MviIntent

/**
 * Compose 화면이 한 번에 렌더링할 수 있는 상태 스냅샷의 공통 표시 계약입니다.
 *
 * 여러 StateFlow를 화면에서 개별 수집하지 않고, 화면 단위 상태 모델 하나로 구독하기 위한 기준입니다.
 */
@Stable
interface UiState

/**
 * Reducer Event는 공통 진행 중 발생한 도메인 이벤트입니다.
 *
 * 이벤트 종류를 타입으로 나눠 ViewModel이나 엔진이 문자열 분기 없이 게임 흐름을 처리하게 합니다.
 */
interface ReducerEvent

/**
 * Intent를 받아 ReducerEvent로 상태를 갱신하는 화면 ViewModel의 공통 기반입니다.
 *
 * 화면은 uiState만 구독하고, 하위 ViewModel은 onIntent와 reduce를 구현해 상태 변경 경로를 한곳에 모읍니다.
 */
abstract class MviViewModel<I : MviIntent, S : UiState, E : ReducerEvent>(
    initialState: S,
) : ViewModel() {
    private val _uiState = MutableStateFlow(initialState)

    /**
     * 화면이 구독하는 유일한 상태 스트림입니다.
     *
     * Compose에서는 `collectAsStateWithLifecycle()`로 이 값을 수집하고, 직접 수정하지 않습니다.
     */
    val uiState: StateFlow<S> = _uiState.asStateFlow()

    /**
     * 현재 상태의 읽기 전용 스냅샷입니다.
     *
     * 비동기 작업을 시작하기 전 현재 값이 필요한 경우에만 사용하고, 상태 변경은 반드시
     * [dispatch]를 통해 수행합니다.
     */
    protected val currentState: S
        get() = _uiState.value

    /**
     * 화면에서 발생한 입력을 처리하는 단일 진입점입니다.
     *
     * 부수 효과가 필요한 작업은 여기서 private 함수로 위임하고, 상태 변경은 [dispatch]로
     * [ReducerEvent]를 발행합니다.
     */
    abstract fun onIntent(intent: I)

    /**
     * 이전 상태와 내부 이벤트를 받아 새 상태를 만드는 순수 함수입니다.
     *
     * 네트워크 호출, 저장소 쓰기, 네비게이션 같은 부수 효과를 넣지 말고 `state.copy(...)`
     * 형태의 상태 계산만 유지합니다.
     */
    protected abstract fun reduce(state: S, event: E): S

    /**
     * 내부 이벤트를 [reduce]에 통과시켜 [uiState]를 갱신합니다.
     *
     * ViewModel 구현체는 `_uiState.update`를 직접 호출하지 않고 이 함수를 사용합니다.
     */
    protected fun dispatch(event: E) {
        _uiState.update { state -> reduce(state, event) }
    }
}
