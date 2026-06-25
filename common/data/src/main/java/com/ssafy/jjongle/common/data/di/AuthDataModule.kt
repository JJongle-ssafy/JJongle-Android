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
 * AuthDataBindingModule에서 사용하는 Hilt 의존성 바인딩을 제공합니다.
 *
 * - 계층: common/data
 * - 책임: 구현체 생성과 주입 범위를 모듈 단위로 모읍니다.
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
 * FirebaseProviderModule에서 사용하는 Hilt 의존성 바인딩을 제공합니다.
 *
 * - 계층: common/data
 * - 책임: 구현체 생성과 주입 범위를 모듈 단위로 모읍니다.
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
