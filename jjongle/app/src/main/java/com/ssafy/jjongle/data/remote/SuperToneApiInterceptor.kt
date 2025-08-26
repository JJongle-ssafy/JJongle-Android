package com.ssafy.jjongle.data.remote

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Named

class SuperToneApiInterceptor @Inject constructor(
    @Named("superToneApiKey") private val apiKey: String
) : Interceptor {
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        
        val newRequest = originalRequest.newBuilder()
            .addHeader("x-sup-api-key", apiKey)
            .build()
        
        return chain.proceed(newRequest)
    }
}
