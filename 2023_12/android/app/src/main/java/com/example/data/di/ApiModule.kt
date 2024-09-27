package com.example.data.di

import android.content.Context
import com.example.data.api.randomuser.RandomUserMapper
import com.example.data.api.randomuser.AuthenticationInterceptor
import com.example.data.api.randomuser.RandomUserMapperImp
import com.example.data.api.randomuser.RandomUserApi
import com.example.example2023.BuildConfig.RANDOM_USER_BASE_URL
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RandomUser

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    private const val HTTP_HEADER_CACHE_CONTROL = "Cache-Control"
    private val contentTypeJson = "application/json".toMediaType()

    private val readTimeout = 30.seconds
    private val writeTimeout = 30.seconds
    private val connectionTimeout = 10.seconds
    private val cacheMaxAge = 10.minutes
    private const val CACHE_FILE_NAME = "HttpCache"
    private const val CACHE_SIZE_BYTES = 10 * 1024 * 1024L // 10 MB

    private val _jsonSerializer = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        isLenient = true
        explicitNulls = false
    }

    @Provides
    @Singleton
    fun provideJsonSerializer() = _jsonSerializer

    @Provides
    @Singleton
    fun provideCache(
        @ApplicationContext app: Context
    ) = Cache(File(app.cacheDir.absolutePath, CACHE_FILE_NAME), CACHE_SIZE_BYTES)

    @Provides
    @Singleton
    fun provideCacheInterceptor() = Interceptor { chain ->
        CacheControl.Builder()
            .maxAge(cacheMaxAge.inWholeMinutes.toInt(), TimeUnit.MINUTES)
            .build().toString()
            .let { cacheControl ->
                chain.proceed(chain.request()).newBuilder()
                    .header(HTTP_HEADER_CACHE_CONTROL, cacheControl)
                    .build()
            }
    }

    @RandomUser
    @Provides
    @Singleton
    fun provideAuthenticatorInterceptor() = AuthenticationInterceptor()

    @Singleton
    @Provides
    fun providesHttpLoggingInterceptor() = HttpLoggingInterceptor()
        .apply { level = HttpLoggingInterceptor.Level.BODY }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        cache: Cache,
        cacheInterceptor: Interceptor,
        @RandomUser authenticationInterceptor: AuthenticationInterceptor,
        httpLoggingInterceptor: HttpLoggingInterceptor,
    ) = OkHttpClient.Builder()
        .connectTimeout(connectionTimeout.toJavaDuration())
        .readTimeout(readTimeout.toJavaDuration())
        .writeTimeout(writeTimeout.toJavaDuration())
        .cache(cache)
        .addInterceptor(cacheInterceptor)
        .addInterceptor(authenticationInterceptor)
        .addInterceptor(httpLoggingInterceptor)
        .build()

    @RandomUser
    @Singleton
    @Provides
    fun provideRetrofitRandomUser(
        json: Json,
        okHttpClient: OkHttpClient,
    ): Retrofit = Retrofit.Builder()
        .baseUrl(RANDOM_USER_BASE_URL)
        .addConverterFactory(json.asConverterFactory(contentTypeJson))
        .client(okHttpClient)
        .build()

    @Singleton
    @Provides
    fun provideRandomUserService(
        @RandomUser retrofit: Retrofit,
    ): RandomUserApi = retrofit.create(RandomUserApi::class.java)

    @Singleton
    @Provides
    fun provideMapper(): RandomUserMapper = RandomUserMapperImp()
}
