package com.pongporn.chatview.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.pongporn.chatview.database.entity.HistoryChatEntity
import retrofit2.http.DELETE

@Dao
interface HistoryChatDao {

    @Insert
    fun insertHistoryChat(historyChatEntity: HistoryChatEntity)

    @Query("SELECT * FROM ChatEntity")
    fun getHisrotyChat() : MutableList<HistoryChatEntity>

    @Query("DELETE FROM ChatEntity")
    fun deleteHistoryChat()
}