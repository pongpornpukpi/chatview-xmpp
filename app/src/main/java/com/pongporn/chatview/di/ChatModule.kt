package com.pongporn.chatview.di

import com.pongporn.chatview.MainActivity
import com.pongporn.chatview.chat.ChatViewActivity
import com.pongporn.chatview.database.ChatDatabase
import com.pongporn.chatview.userlist.UserListActivity
import com.pongporn.chatview.utils.PreferenceUtils
import com.pongporn.chatview.utils.XMPP
import com.pongporn.chatview.viewmodel.ChatViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

fun provideChatModule() = module {
    single { XMPP() }
}

fun provideDatabase() = module {
    single { ChatDatabase.getInstance(androidContext()) }
    single { PreferenceUtils.instance }
}

fun provideUIModule() = module {
    factory { MainActivity.MyLoginTask(androidContext(), get()) }
    factory { MainActivity() }
    factory { ChatViewActivity() }
    factory { UserListActivity() }
    viewModel { ChatViewModel(get(),get(),get(),get()) }
}