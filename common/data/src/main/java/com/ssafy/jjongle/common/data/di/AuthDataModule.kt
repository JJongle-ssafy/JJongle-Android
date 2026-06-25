package com.ssafy.jjongle.common.data.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ssafy.jjongle.common.data.firebase.FirebaseAuthDataSource
import com.ssafy.jjongle.common.data.firebase.FirebaseAuthDataSourceImpl
import com.ssafy.jjongle.common.data.firebase.FirestoreUserProfileDataSource
import com.ssafy.jjongle.common.data.firebase.UserProfileDataSource
import com.ssafy.jjongle.common.data.repository.AuthRepositoryImpl
import com.ssafy.jjongle.common.data.service.GoogleAuthServiceImpl
import com.ssafy.jjongle.common.domain.repository.AuthRepository
import com.ssafy.jjongle.common.domain.repository.GoogleAuthService
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Auth Data Binding 관련 구현체를 Hilt 그래프에 연결하는 DI 모듈입니다.
 *
 * 인터페이스와 구현체의 바인딩 위치를 한곳에 모아 feature 모듈이 생성 방식에 직접 의존하지 않게 합니다.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class AuthDataBindingModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindGoogleAuthService(
        googleAuthServiceImpl: GoogleAuthServiceImpl
    ): GoogleAuthService

    @Binds
    @Singleton
    abstract fun bindFirebaseAuthDataSource(
        impl: FirebaseAuthDataSourceImpl
    ): FirebaseAuthDataSource

    @Binds
    @Singleton
    abstract fun bindUserProfileDataSource(
        impl: FirestoreUserProfileDataSource
    ): UserProfileDataSource
}

/**
 * Firebase Provider 관련 구현체를 Hilt 그래프에 연결하는 DI 모듈입니다.
 *
 * 인터페이스와 구현체의 바인딩 위치를 한곳에 모아 feature 모듈이 생성 방식에 직접 의존하지 않게 합니다.
 */
@Module
@InstallIn(SingletonComponent::class)
object FirebaseProviderModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()
}
