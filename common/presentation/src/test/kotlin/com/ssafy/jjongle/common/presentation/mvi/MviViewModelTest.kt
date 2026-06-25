package com.ssafy.jjongle.common.presentation.mvi

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Mvi View Model Test 회귀를 막기 위한 테스트입니다.
 *
 * 리팩터링이나 모듈 이동 중에도 기존 동작, 아키텍처 경계, 오류 처리 계약이 유지되는지 확인합니다.
 */
class MviViewModelTest {

    @Test
    fun ui_state_starts_with_initial_state() {
        val viewModel = CounterViewModel()

        assertEquals(CounterState(count = 0), viewModel.uiState.value)
    }

    @Test
    fun on_intent_dispatches_reducer_event_through_reduce_only() {
        val viewModel = CounterViewModel()

        viewModel.onIntent(CounterIntent.Increment)
        viewModel.onIntent(CounterIntent.Increment)

        assertEquals(CounterState(count = 2), viewModel.uiState.value)
        assertEquals(CounterState(count = 2), viewModel.exposedCurrentState())
    }

    private sealed interface CounterIntent : MviIntent {
        data object Increment : CounterIntent
    }

    private data class CounterState(
        val count: Int
    ) : UiState

    private sealed interface CounterEvent : ReducerEvent {
        data object Incremented : CounterEvent
    }

    private class CounterViewModel : MviViewModel<CounterIntent, CounterState, CounterEvent>(
        initialState = CounterState(count = 0)
    ) {
        override fun onIntent(intent: CounterIntent) {
            when (intent) {
                CounterIntent.Increment -> dispatch(CounterEvent.Incremented)
            }
        }

        override fun reduce(state: CounterState, event: CounterEvent): CounterState {
            return when (event) {
                CounterEvent.Incremented -> state.copy(count = state.count + 1)
            }
        }

        fun exposedCurrentState(): CounterState = currentState
    }
}
