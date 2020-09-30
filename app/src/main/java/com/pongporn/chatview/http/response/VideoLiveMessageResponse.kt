package com.pongporn.chatview.http.response

data class VideoLiveMessageResponse(
    var etag: String? = "",
    var items: List<Item>? = listOf(),
    var kind: String? = "",
    var nextPageToken: String? = "",
    var pageInfo: PageInfo? = PageInfo(),
    var pollingIntervalMillis: Long? = 0
) {
    data class Item(
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

    data class PageInfo(
        var resultsPerPage: Int? = 0,
        var totalResults: Int? = 0
    )


}



