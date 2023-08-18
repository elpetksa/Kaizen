package com.elpet.kaizen.di

import com.elpet.kaizen.BuildConfig
import com.elpet.kaizen.config.API_BASE_URL
import com.elpet.kaizen.data.api.ApiInterceptor
import com.elpet.kaizen.data.api.ApiService
import com.elpet.kaizen.util.API_TIMEOUT
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    /**
     * Constructs and provides a singleton [OkHttpClient] used for [Retrofit] instance
     * initialization.
     *
     * @return A [OkHttpClient] instance.
     */
    @Singleton
    @Provides
    fun provideOkHttpClient(
        apiInterceptor: ApiInterceptor,
        httpLoggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(apiInterceptor)
            .addInterceptor(httpLoggingInterceptor)
            .readTimeout(API_TIMEOUT, TimeUnit.SECONDS)
            .connectTimeout(API_TIMEOUT, TimeUnit.SECONDS)
            .build()
    }

    /**
     * Constructs and provides a singleton [Retrofit] instance used to perform api calls.
     */
    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        converterFactory: GsonConverterFactory
    ): Retrofit = Retrofit.Builder()
        .addConverterFactory(converterFactory)
        .baseUrl(API_BASE_URL)
        .client(okHttpClient)
        .build()

    /**
     * Constructs and provides a singleton [HttpLoggingInterceptor] instance.
     *
     * @return A [HttpLoggingInterceptor] instance.
     */
    @Singleton
    @Provides
    fun providesHttpLoggingInterceptor() = HttpLoggingInterceptor()
        .apply {
            level = when (BuildConfig.DEBUG) {
                true -> HttpLoggingInterceptor.Level.BODY
                false -> HttpLoggingInterceptor.Level.NONE
            }
        }

    /**
     * Constructs and provides a singleton [OkHttpClient] used for [Retrofit] instance
     * initialization.
     *
     * @return A [OkHttpClient] instance.
     */
    @Singleton
    @Provides
    fun provideConverterFactory(): GsonConverterFactory = GsonConverterFactory.create()

    /**
     * Constructs and provides a singleton [ApiService] instance.
     *
     * @return A [ApiService] instance.
     */
    @Provides
    @Singleton
    fun provideApiService(
        retrofit: Retrofit
    ): ApiService = retrofit.create(ApiService::class.java)

}