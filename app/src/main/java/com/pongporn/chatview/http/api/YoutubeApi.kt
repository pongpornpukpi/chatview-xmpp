package com.pongporn.chatview.http.api

import com.pongporn.chatview.http.response.VideoDataResponseModel
import io.reactivex.Observable
import io.reactivex.Observer
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface YoutubeApi {

    @GET("videos")
    fun getViedoDisplay(@Query("id") id : String, @Query("key") key : String, @Query("part") part : String = "snippet") : Observable<VideoDataResponseModel>

}