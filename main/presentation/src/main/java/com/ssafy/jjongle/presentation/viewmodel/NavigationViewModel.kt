package com.ssafy.jjongle.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.jjongle.main.domain.repository.NavigationStateRepository
import dagger.hilt.android.lifecycle.HiltViewModel // Hilt 사용 시
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * NavigationViewModel 화면 상태와 이벤트를 처리하는 ViewModel입니다.
 *
 * - 계층: main/presentation
 * - 책임: 유스케이스를 호출하고 UI가 구독할 상태 흐름을 제공합니다.
 */
@HiltViewModel // Hilt 사용 시
class NavigationViewModel @Inject constructor( // Hilt 사용 시 @Inject
    private val navigationStateRepository: NavigationStateRepository
) : ViewModel() { // 일반 ViewModel로 변경

    private val _currentRoute = MutableStateFlow<String?>(null)
    val currentRoute: StateFlow<String?> = _currentRoute.asStateFlow()

    init {
        loadSavedRoute()
    }

    private fun loadSavedRoute() {
        viewModelScope.launch {
            // Repository를 통해 비동기적으로 로드
            _currentRoute.value = navigationStateRepository.getCurrentRoute()
        }
    }

    fun saveRoute(route: String) {
        viewModelScope.launch {
            navigationStateRepository.saveCurrentRoute(route)
            _currentRoute.value = route // UI 즉시 반영 (선택적)
        }
    }

}
