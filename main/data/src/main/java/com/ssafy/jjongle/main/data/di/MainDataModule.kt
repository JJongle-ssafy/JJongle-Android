package com.ssafy.jjongle.main.data.di

import com.ssafy.jjongle.main.data.repository.NavigationStateRepositoryImpl
import com.ssafy.jjongle.main.domain.repository.NavigationStateRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * MainDataModule에서 사용하는 Hilt 의존성 바인딩을 제공합니다.
 *
 * - 계층: main/data
 * - 책임: 구현체 생성과 주입 범위를 모듈 단위로 모읍니다.
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
