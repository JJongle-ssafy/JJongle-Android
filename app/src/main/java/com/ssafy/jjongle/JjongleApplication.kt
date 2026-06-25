package com.ssafy.jjongle

import android.app.Application
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.ssafy.jjongle.common.domain.repository.BgmRepository
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

/**
 * 쫑글 앱의 Application 진입점을 구성합니다.
 *
 * - 계층: app
 * - 책임: 전역 초기화와 앱 수준 의존성 설정의 시작점을 제공합니다.
 */
@HiltAndroidApp
class JjongleApplication : Application(), DefaultLifecycleObserver {

    // BGM 인터페이스를 주입받아 앱 생명주기에 따라 배경음악을 제어합니다.
    // 백그라운드로 가면 일시정지, 다시 포그라운드면 재개
    @Inject
    lateinit var bgm: BgmRepository

    override fun onCreate() {
        super<Application>.onCreate()
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    override fun onStop(owner: LifecycleOwner) { // 앱이 백그라운드로
        bgm.pause()
    }

    override fun onStart(owner: LifecycleOwner) { // 포그라운드 복귀
        bgm.resume()
    }

    // 앱 최소화 중에도 계속 재생하고 싶다면 위 pause/resume 콜을 제거하고, Foreground Service + 알림으로 승격하면 됨
}
