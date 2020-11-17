package com.pongporn.chatview.http.request
import com.google.gson.annotations.SerializedName


data class InsertVideoLiveChatMessageRequest(
    @SerializedName("snippet")
    var snippet: Snippet? = Snippet()
) {
    data class Snippet(
        @SerializedName("liveChatId")
        var liveChatId: String? = "",
        @SerializedName("textMessageDetails")
        var textMessageDetails: TextMessageDetails? = TextMessageDetails(),
        @SerializedName("type")
        var type: String? = ""
    ) {
        data class TextMessageDetails(
            @SerializedName("messageText")
            var messageText: String? = ""
        )
    }
}




