package com.tubes.fittrack.ninjasApi

import com.tubes.fittrack.caloriesApi.FoodItem
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Query

interface ApiService {
    @Headers("X-Api-Key: WXHNixkWnzfwDt788KwmSQ==TiyqAmEbtpAYlIYJ")
    @GET("caloriesburnedactivities")
    suspend fun getActivities(): Response<ActivityResponse>
    @Headers("X-Api-Key: WXHNixkWnzfwDt788KwmSQ==TiyqAmEbtpAYlIYJ")
    @GET("caloriesburned?activity=")
    suspend fun getDetail(@Query("activity") activity: String): Response<DetailResponse>

    @GET("caloriesburned")
    fun getCaloriesBurned(
        @Header("X-Api-Key") apiKey: String,
        @Query("activity") activity: String
    ): Call<List<ActivityItem>>

    @GET("nutrition")
    fun getFoodInfo(
        @Header("X-Api-Key") apiKey: String,
        @Query("query") query: String
    ): Call<List<FoodItem>>

}
