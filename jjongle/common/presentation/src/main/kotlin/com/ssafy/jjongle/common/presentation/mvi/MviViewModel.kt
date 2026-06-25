package com.ssafy.jjongle.common.presentation.mvi

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * MviIntent 화면에서 ViewModel로 전달되는 사용자 입력을 정의합니다.
 *
 * - 계층: common/presentation
 * - 책임: UI 이벤트를 MVI intent로 분리해 상태 변경 진입점을 명확히 합니다.
 */
interface MviIntent

/**
 * UiState 화면이 구독하는 상태 모델입니다.
 *
 * - 계층: common/presentation
 * - 책임: 렌더링에 필요한 값을 한곳에 모아 UI와 상태 변경 로직을 분리합니다.
 */
@Stable
interface UiState

/**
 * ReducerEvent ViewModel 내부 상태 변경 이벤트를 정의합니다.
 *
 * - 계층: common/presentation
 * - 책임: 비동기 결과와 사용자 입력을 reducer가 처리할 수 있는 이벤트로 정리합니다.
 */
interface ReducerEvent

/**
 * MviViewModel 화면 상태와 이벤트를 처리하는 ViewModel입니다.
 *
 * - 계층: common/presentation
 * - 책임: 유스케이스를 호출하고 UI가 구독할 상태 흐름을 제공합니다.
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
