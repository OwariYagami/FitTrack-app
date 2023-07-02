package com.tubes.fittrack.api

data class ResponseUserProfile(
    val status: Boolean,
    val name: String,
    val data: Data
)

data class Data(
    val id: Int,
    val id_user: Int,
    val usia: Int?,
    val kelamin: String?,
    val b_badan: Int?,
    val t_badan: Int?,
    val kalori: Int?,
    val image: String?
)
