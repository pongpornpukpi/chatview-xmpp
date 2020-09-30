package com.pongporn.chatview.http.response

data class PostGoogleAuth(
    var access_token: String? = "",
    var expires_in: Int? = 0,
    var refresh_token: String? = "",
    var token_type: String? = ""
)