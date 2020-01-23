package com.pongporn.chatview.di

import android.content.Context
import com.pongporn.chatview.BuildConfig
import com.pongporn.chatview.http.api.YoutubeApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

fun provideHttpModule() = module {
    factory { provideOkHttpClient() }
    factory { provideRetrofit(androidContext(),get()) }
    factory { provideYoutubeChatAPI(get()) }
}

fun provideOkHttpClient(): OkHttpClient {
    val interceptor = HttpLoggingInterceptor()
    if (BuildConfig.DEBUG) {
        interceptor.level = HttpLoggingInterceptor.Level.BODY
    }
    return OkHttpClient.Builder().apply {
        connectTimeout(60, TimeUnit.SECONDS)
        readTimeout(60, TimeUnit.SECONDS)
        writeTimeout(60, TimeUnit.SECONDS)
        addInterceptor(interceptor)
        addInterceptor { chain ->
            return@addInterceptor chain.proceed(chain.request())
        }
    }.build()
}

fun provideRetrofit(context: Context, okHttpClient: OkHttpClient): Retrofit {
    return Retrofit.Builder()
        .baseUrl("https://www.googleapis.com/youtube/v3/")
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .client(okHttpClient)
        .build()
}

fun provideYoutubeChatAPI(retrofit: Retrofit) : YoutubeApi {
    return retrofit.create(YoutubeApi::class.java)
}