package com.pongporn.chatview.http.api

import com.pongporn.chatview.http.request.InsertVideoLiveChatMessageRequest
import com.pongporn.chatview.http.response.InsertVideoLiveMessage
import com.pongporn.chatview.http.response.VideoDataResponseModel
import com.pongporn.chatview.http.response.VideoLiveMessageResponse
import com.pongporn.chatview.http.response.VideoLiveStreamingDetailResponseModel
import io.reactivex.Observable
import io.reactivex.Observer
import retrofit2.Response
import retrofit2.http.*

interface YoutubeApi {

    @GET("videos")
    fun getViedoData(@Query("id") id : String, @Query("key") key : String, @Query("part") part : String = "snippet") : Observable<VideoDataResponseModel>

    @GET("videos")
    fun getViedoLiveStraming(@Query("id") id : String, @Query("key") key : String, @Query("part") part : String = "liveStreamingDetails") : Observable<VideoLiveStreamingDetailResponseModel>

    @GET("liveChat/messages")
    fun getMessageLiveStreaming(@Query("liveChatId") id : String, @Query("key") key : String, @Query("part") part : String = "snippet", @Query("part") part2 : String = "authorDetails", @Query("profileImageSize") profileImageSize : String = "50") : Observable<VideoLiveMessageResponse>

    @GET("liveChat/messages")
    fun getMessageLiveStreamingRealTime(@Query("liveChatId") id : String, @Query("key") key : String, @Query("part") part : String = "snippet", @Query("part") part2 : String = "authorDetails", @Query("profileImageSize") profileImageSize : String = "50", @Query("pageToken") pageToken : String) : Observable<VideoLiveMessageResponse>

    @POST("liveChat/messages")
    fun insertLiveChatMessages(@Header("Authorization") accessToken : String, @Query("part") part : String = "snippet", @Query("key") key : String, @Body insertVideoLiveChatMessageRequest: InsertVideoLiveChatMessageRequest) : Observable<InsertVideoLiveMessage>
}