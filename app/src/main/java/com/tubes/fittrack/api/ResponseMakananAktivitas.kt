package com.tubes.fittrack.api

data class ResponseMakananAktivitas (
    val status: Boolean,
    val data: Datama
    )

data class Datama(
    val tanggal: String,
    val makanan: List<Food>,
    val aktivitas: List<Activity>
)

data class Food(
    val name: String,
    val takaran: String,
    val kalori: String
)

data class Activity(
    val name: String,
    val durasi: Int,
    val kalori: Int
)