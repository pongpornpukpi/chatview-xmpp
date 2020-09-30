package com.pongporn.chatview.http.request

data class InsertVideoLiveChatMessageRequest(
    var snippet: Snippet? = Snippet()
) {
    data class Snippet(
        var liveChatId: String? = "",
        var textMessageDetails: TextMessageDetails? = TextMessageDetails(),
        var type: String? = ""
    ) {
        data class TextMessageDetails(
            var messageText: String? = ""
        )
    }
}



