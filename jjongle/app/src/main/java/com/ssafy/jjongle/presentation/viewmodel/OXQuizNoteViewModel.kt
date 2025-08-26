package com.ssafy.jjongle.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.jjongle.domain.entity.OXGameWrongAnswerNote
import com.ssafy.jjongle.domain.usecase.GetOXGameHistoriesUseCase
import com.ssafy.jjongle.domain.usecase.GetOXGameHistoryDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

data class QuizNoteUi(val id: Long, val recordedAt: LocalDateTime)

data class QuizNoteState(
    val notes: List<QuizNoteUi> = emptyList(),
    val page: Int = 0,
    val hasNext: Boolean = false,   // ★ totalPages 대신
    val loading: Boolean = false,
    val error: String? = null,
    val detail: List<OXGameWrongAnswerNote> = emptyList()
)

@HiltViewModel
class QuizNoteViewModel @Inject constructor(
    private val histories: GetOXGameHistoriesUseCase,
    private val historyDetail: GetOXGameHistoryDetailUseCase

) : ViewModel() {

    private val _ui = MutableStateFlow(QuizNoteState())
    val ui: StateFlow<QuizNoteState> = _ui

    private val PAGE_SIZE = 3

    fun loadPage(page: Int) {
        viewModelScope.launch {

            _ui.value = _ui.value.copy(loading = true, error = null)
            Log.d("loadPage", "loadPage: api 실행함")
            runCatching { histories(page) }
                .onSuccess { res ->
                    val items = res.content.map { h -> QuizNoteUi(h.id, h.playedAt) }
                    _ui.value = _ui.value.copy(
                        notes = items,
                        page = page,
                        hasNext = if (res.totalPages > 0) page < res.totalPages - 1 else items.size >= PAGE_SIZE,
                        loading = false
                    )
                }
                .onFailure { e ->
                    _ui.value = _ui.value.copy(loading = false, error = e.message)
                }
        }
    }

    fun openDetail(historyId: Long) {
        viewModelScope.launch {
            _ui.value = _ui.value.copy(loading = true, error = null)
            runCatching { historyDetail(historyId) }
                .onSuccess { list ->
                    _ui.value = _ui.value.copy(detail = list, loading = false)
                }
                .onFailure { e ->
                    _ui.value = _ui.value.copy(loading = false, error = e.message)
                }
        }
    }

    fun closeDetail() {
        _ui.value = _ui.value.copy(detail = emptyList())
    }
}
