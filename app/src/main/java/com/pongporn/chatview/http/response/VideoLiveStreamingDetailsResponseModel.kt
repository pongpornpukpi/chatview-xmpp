package com.pongporn.chatview.http.response

data class VideoLiveStreamingDetailResponseModel(
    var etag: String? = "",
    var items: List<Item>? = listOf(),
    var kind: String? = "",
    var pageInfo: PageInfo? = PageInfo()
) {
    data class Item(
        var etag: String? = "",
        var id: String? = "",
        var kind: String? = "",
        var liveStreamingDetails: LiveStreamingDetails? = LiveStreamingDetails()
    ) {
        data class LiveStreamingDetails(
            var activeLiveChatId: String? = "",
            var actualStartTime: String? = "",
            var concurrentViewers: String? = "",
            var scheduledStartTime: String? = ""
        )
    }

    data class PageInfo(
        var resultsPerPage: Int? = 0,
        var totalResults: Int? = 0
    )
}


