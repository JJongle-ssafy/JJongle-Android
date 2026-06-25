package com.ssafy.jjongle.common.data.di

import com.ssafy.jjongle.common.data.BuildConfig
import com.ssafy.jjongle.common.data.remote.PrettyHttpLoggingInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory

/**
 * NetworkModule에서 사용하는 Hilt 의존성 바인딩을 제공합니다.
 *
 * - 계층: common/data
 * - 책임: 구현체 생성과 주입 범위를 모듈 단위로 모읍니다.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
        coerceInputValues = true
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val prettyLoggingInterceptor = PrettyHttpLoggingInterceptor()
        return OkHttpClient.Builder()
            .addInterceptor(prettyLoggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, json: Json): Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
}
