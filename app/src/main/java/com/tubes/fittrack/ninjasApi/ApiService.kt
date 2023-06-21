package com.tubes.fittrack.ninjasApi

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers

interface ApiService {
    @Headers("X-Api-Key: WXHNixkWnzfwDt788KwmSQ==TiyqAmEbtpAYlIYJ")
    @GET("caloriesburnedactivities")
    suspend fun getActivities(): Response<ActivityResponse>

}
