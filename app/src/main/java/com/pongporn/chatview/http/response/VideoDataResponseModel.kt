package com.pongporn.chatview.http.response
import com.google.gson.annotations.SerializedName


data class VideoDataResponseModel(
    @SerializedName("etag")
    var etag: String? = "",
    @SerializedName("items")
    var items: List<Item?>? = listOf(),
    @SerializedName("kind")
    var kind: String? = "",
    @SerializedName("pageInfo")
    var pageInfo: PageInfo? = PageInfo()
)

data class Item(
    @SerializedName("etag")
    var etag: String? = "",
    @SerializedName("id")
    var id: String? = "",
    @SerializedName("kind")
    var kind: String? = "",
    @SerializedName("snippet")
    var snippet: Snippet? = Snippet()
)

data class Snippet(
    @SerializedName("categoryId")
    var categoryId: String? = "",
    @SerializedName("channelId")
    var channelId: String? = "",
    @SerializedName("channelTitle")
    var channelTitle: String? = "",
    @SerializedName("description")
    var description: String? = "",
    @SerializedName("liveBroadcastContent")
    var liveBroadcastContent: String? = "",
    @SerializedName("localized")
    var localized: Localized? = Localized(),
    @SerializedName("publishedAt")
    var publishedAt: String? = "",
    @SerializedName("tags")
    var tags: List<String?>? = listOf(),
    @SerializedName("thumbnails")
    var thumbnails: Thumbnails? = Thumbnails(),
    @SerializedName("title")
    var title: String? = ""
)

data class Localized(
    @SerializedName("description")
    var description: String? = "",
    @SerializedName("title")
    var title: String? = ""
)

data class Thumbnails(
    @SerializedName("default")
    var default: Default? = Default(),
    @SerializedName("high")
    var high: High? = High(),
    @SerializedName("maxres")
    var maxres: Maxres? = Maxres(),
    @SerializedName("medium")
    var medium: Medium? = Medium(),
    @SerializedName("standard")
    var standard: Standard? = Standard()
)

data class Default(
    @SerializedName("height")
    var height: Int? = 0,
    @SerializedName("url")
    var url: String? = "",
    @SerializedName("width")
    var width: Int? = 0
)

data class High(
    @SerializedName("height")
    var height: Int? = 0,
    @SerializedName("url")
    var url: String? = "",
    @SerializedName("width")
    var width: Int? = 0
)

data class Maxres(
    @SerializedName("height")
    var height: Int? = 0,
    @SerializedName("url")
    var url: String? = "",
    @SerializedName("width")
    var width: Int? = 0
)

data class Medium(
    @SerializedName("height")
    var height: Int? = 0,
    @SerializedName("url")
    var url: String? = "",
    @SerializedName("width")
    var width: Int? = 0
)

data class Standard(
    @SerializedName("height")
    var height: Int? = 0,
    @SerializedName("url")
    var url: String? = "",
    @SerializedName("width")
    var width: Int? = 0
)

data class PageInfo(
    @SerializedName("resultsPerPage")
    var resultsPerPage: Int? = 0,
    @SerializedName("totalResults")
    var totalResults: Int? = 0
)