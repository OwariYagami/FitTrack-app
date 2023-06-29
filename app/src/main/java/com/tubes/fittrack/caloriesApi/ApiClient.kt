package com.tubes.fittrack.caloriesApi


import com.tubes.fittrack.api.RetrofitClient
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val BASE_URL = "https://api.api-ninjas.com/v1/"
    private const val API_KEY = "WXHNixkWnzfwDt788KwmSQ==TiyqAmEbtpAYlIYJ"
    private val okHttpClient: OkHttpClient by lazy {
        val httpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("X-Api-Key", API_KEY) // Tambahkan header Authorization dengan API key
                    .build()
                chain.proceed(request)
            }
            .build()

        httpClient
    }
    private val retrofit: Retrofit by lazy {


        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}