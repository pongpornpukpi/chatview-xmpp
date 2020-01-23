package com.pongporn.chatview.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.pongporn.chatview.database.dao.HistoryChatDao
import com.pongporn.chatview.database.entity.HistoryChatEntity

@Database(entities = [HistoryChatEntity::class],version = 1,exportSchema = false)
abstract class ChatDatabase : RoomDatabase() {

    abstract fun historyChatDao() : HistoryChatDao

    companion object {
        private var INSTANCE : ChatDatabase? = null
        fun getInstance(context : Context) : ChatDatabase? {
            if(INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context,ChatDatabase::class.java,"chat_database.db")
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build()
            }
            return INSTANCE
        }
    }

}