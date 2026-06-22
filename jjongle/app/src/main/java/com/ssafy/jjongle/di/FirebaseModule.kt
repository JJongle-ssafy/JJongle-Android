package com.ssafy.jjongle.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ssafy.jjongle.data.firebase.FirebaseAuthDataSource
import com.ssafy.jjongle.data.firebase.FirebaseAuthDataSourceImpl
import com.ssafy.jjongle.data.firebase.FirestoreUserProfileDataSource
import com.ssafy.jjongle.data.firebase.UserProfileDataSource
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FirebaseBindingModule {

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
