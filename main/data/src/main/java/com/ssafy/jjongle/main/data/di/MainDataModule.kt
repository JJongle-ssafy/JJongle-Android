package com.ssafy.jjongle.main.data.di

import com.ssafy.jjongle.main.data.repository.NavigationStateRepositoryImpl
import com.ssafy.jjongle.main.domain.repository.NavigationStateRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Main Data 관련 구현체를 Hilt 그래프에 연결하는 DI 모듈입니다.
 *
 * 인터페이스와 구현체의 바인딩 위치를 한곳에 모아 feature 모듈이 생성 방식에 직접 의존하지 않게 합니다.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class MainDataModule {

    @Binds
    @Singleton
    abstract fun bindNavigationStateRepository(
        navigationStateRepositoryImpl: NavigationStateRepositoryImpl,
    ): NavigationStateRepository
}
