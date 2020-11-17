package com.pongporn.chatview.model

data class ChatMessageModel (
    var timestamp : String? = "",
    var message : String? = "",
    var name : String? = "",
    var imageUrl : String? = ""
)