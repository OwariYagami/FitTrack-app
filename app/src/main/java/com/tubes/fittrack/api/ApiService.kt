package com.tubes.fittrack.api

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path


interface ApiService {
    @FormUrlEncoded
    @POST("user/login")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String,
    ): Call<Users>

    @FormUrlEncoded
    @POST("user/register")
    fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("c_pass") c_pass: String
    ): Call<ResponseRegister>

    @GET("user/profil/{email}")
    fun getUserProfil(@Path("email") email: String): Call<ResponseUserProfile>

    @POST("user/profil/{email}")
    @Multipart
    fun uploadProfile(
        @Path("email") email: String,
        @Part imagePart: MultipartBody.Part?,
        @Part("name") name: RequestBody,
        @Part("usia") usia: RequestBody,
        @Part("kelamin") kelamin: RequestBody,
        @Part("b_badan") b_badan: RequestBody,
        @Part("t_badan") t_badan: RequestBody,
        @Part("kalori") kalori: RequestBody
    ): Call<ResponseUpdateProfil>

    @GET("location")
    fun getLocation(): Call<ResponseLocation>

    @FormUrlEncoded
    @POST("tanggal/makanan/{email}")
    fun tambahMakanan(
        @Path("email") email: String,
        @Field("name") name: String,
        @Field("takaran") takaran: String,
        @Field("kalori") kalori: String
    ): Call<ResponseTambahMakanan>

    @FormUrlEncoded
    @POST("tanggal/aktivitas/{email}")
    fun tambahLatihan(
        @Path("email") email: String,
        @Field("name") name: String,
        @Field("durasi") takaran: String,
        @Field("kalori") kalori: String
    ): Call<ResponseTambahMakanan>

    @GET("all/aktivitas/sekarang/{email}")
    fun getDataMakananAktivitas(@Path("email") email: String): Call<ResponseMakananAktivitas>

    @GET("all/aktivitas/kemarin/{email}")
    fun getDatakemarin(@Path("email") email: String): Call<ResponseMakananAktivitas>

}
