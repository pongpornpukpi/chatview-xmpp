package com.pongporn.chatview.http.api

import com.pongporn.chatview.http.response.PostGoogleAuth
import io.reactivex.Observable
import org.json.JSONObject
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Url

interface GoogleApi {

    @POST
    fun oAuthGoogleAPI(@Url url: String = "https://accounts.google.com/o/oauth2/auth",@Query("client_id") client_id : String = "694211781230-dp7su021ospdighpbbbgdtc8s516u6ob.apps.googleusercontent.com",@Query("redirect_uri") redirect_uri : String = "com.pongporn.chatview:redirect_uri_path",@Query("response_type") response_type : String = "code",@Query("scope") scope : String = "https://www.googleapis.com/auth/youtube") : Observable<JSONObject>

}