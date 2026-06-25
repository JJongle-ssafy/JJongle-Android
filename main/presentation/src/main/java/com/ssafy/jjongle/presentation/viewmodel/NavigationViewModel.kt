package com.ssafy.jjongle.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.jjongle.main.domain.repository.NavigationStateRepository
import dagger.hilt.android.lifecycle.HiltViewModel // Hilt 사용 시
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Navigation 화면의 사용자 입력과 비동기 결과를 UI 상태로 변환하는 ViewModel입니다.
 *
 * UseCase 호출, 오류 처리, 상태 전이를 한곳에 모아 Compose 화면은 상태 구독과 Intent 전달에 집중하도록 합니다.
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
