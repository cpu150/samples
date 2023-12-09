package com.example.data.api.randomuser

import okhttp3.Interceptor

class AuthenticationInterceptor : Interceptor {

    companion object {
        private const val AUTH_HEADER = "Authorization"
        private const val API_KEY = "Jw0oIMgpId1"
    }

    override fun intercept(chain: Interceptor.Chain) = chain.proceed(
        chain
            .request()
            .newBuilder()
            .addHeader(AUTH_HEADER, "BEARER $API_KEY")
            .build()
    )
}
