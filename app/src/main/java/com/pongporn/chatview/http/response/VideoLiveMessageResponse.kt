package com.pongporn.chatview.http.response
import com.google.gson.annotations.SerializedName

data class VideoLiveMessageResponse(
    @SerializedName("etag")
    var etag: String? = "",
    @SerializedName("items")
    var items: List<Item>? = listOf(),
    @SerializedName("kind")
    var kind: String? = "",
    @SerializedName("nextPageToken")
    var nextPageToken: String? = "",
    @SerializedName("pageInfo")
    var pageInfo: PageInfo? = PageInfo(),
    @SerializedName("pollingIntervalMillis")
    var pollingIntervalMillis: Long? = 0
) {
    data class Item(
        @SerializedName("authorDetails")
        var authorDetails: AuthorDetails? = AuthorDetails(),
        @SerializedName("etag")
        var etag: String? = "",
        @SerializedName("id")
        var id: String? = "",
        @SerializedName("kind")
        var kind: String? = "",
        @SerializedName("snippet")
        var snippet: Snippet? = Snippet()
    ) {

        data class AuthorDetails(
            @SerializedName("channelId")
            var channelId: String? = "",
            @SerializedName("channelUrl")
            var channelUrl: String? = "",
            @SerializedName("displayName")
            var displayName: String? = "",
            @SerializedName("isChatModerator")
            var isChatModerator: Boolean? = false,
            @SerializedName("isChatOwner")
            var isChatOwner: Boolean? = false,
            @SerializedName("isChatSponsor")
            var isChatSponsor: Boolean? = false,
            @SerializedName("isVerified")
            var isVerified: Boolean? = false,
            @SerializedName("profileImageUrl")
            var profileImageUrl: String? = ""
        )

        data class Snippet(
            @SerializedName("authorChannelId")
            var authorChannelId: String? = "",
            @SerializedName("displayMessage")
            var displayMessage: String? = "",
            @SerializedName("hasDisplayContent")
            var hasDisplayContent: Boolean? = false,
            @SerializedName("liveChatId")
            var liveChatId: String? = "",
            @SerializedName("publishedAt")
            var publishedAt: String? = "",
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

    data class PageInfo(
        @SerializedName("resultsPerPage")
        var resultsPerPage: Int? = 0,
        @SerializedName("totalResults")
        var totalResults: Int? = 0
    )

}


