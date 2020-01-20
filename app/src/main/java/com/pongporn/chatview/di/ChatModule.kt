package com.pongporn.chatview.di

import com.pongporn.chatview.MainActivity
import com.pongporn.chatview.chat.ChatViewActivity
import com.pongporn.chatview.utils.XMPP
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

fun provideChatModule() = module {
    single { XMPP() }
}

fun provideUIModule() = module {
    factory { MainActivity.MyLoginTask(androidContext(), get()) }
    factory { MainActivity() }
    factory { ChatViewActivity() }
}