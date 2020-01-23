package com.pongporn.chatview.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ChatEntity")
data class HistoryChatEntity (
    @PrimaryKey(autoGenerate = true)
    var id : Int = 0,
    @ColumnInfo(name = "timeStamp")
    var timeStamp : String? = "",
    @ColumnInfo(name = "fromTo")
    var fromTo : String? = "",
    @ColumnInfo(name = "message")
    var message : String? = ""
)