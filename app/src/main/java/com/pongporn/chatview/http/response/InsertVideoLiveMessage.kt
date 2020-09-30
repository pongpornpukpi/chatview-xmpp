package com.pongporn.chatview.http.response

data class InsertVideoLiveMessage(
    var etag: String? = "",
    var id: String? = "",
    var kind: String? = "",
    var snippet: Snippet? = Snippet()
) {
    data class Snippet(
        var authorChannelId: String? = "",
        var displayMessage: String? = "",
        var hasDisplayContent: Boolean? = false,
        var liveChatId: String? = "",
        var publishedAt: String? = "",
        var textMessageDetails: TextMessageDetails? = TextMessageDetails(),
        var type: String? = ""
    ) {
        data class TextMessageDetails(
            var messageText: String? = ""
        )
    }
}



