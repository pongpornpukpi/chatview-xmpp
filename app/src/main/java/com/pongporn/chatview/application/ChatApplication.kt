package com.pongporn.chatview.application

import android.app.Application
import com.mohamadamin.kpreferences.base.KPreferenceManager
import com.pongporn.chatview.di.provideChatModule
import com.pongporn.chatview.di.provideDatabase
import com.pongporn.chatview.di.provideUIModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidFileProperties
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class ChatApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        KPreferenceManager.initialize(this@ChatApplication)
        startKoin {
            androidLogger()
            androidContext(this@ChatApplication)
            androidFileProperties()
            modules(
                listOf(
                    provideChatModule(),
                    provideUIModule(),
                    provideDatabase()
                )
            )
        }
    }

}