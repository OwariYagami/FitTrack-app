package com.tubes.fittrack.caloriesApi




import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Query

interface ApiService {
    @Headers("X-Api-Key: WXHNixkWnzfwDt788KwmSQ==1nglvmA0IZhLw7kV")
    @GET("nutrition?query=")
    fun getDetail(@Query("query") query: String): Call<DetailResponse>

    @GET("nutrition")
    fun getFoodInfo(
        @Header("X-Api-Key") apiKey: String,
        @Query("query") query: String
    ): Call<List<FoodItem>>
}