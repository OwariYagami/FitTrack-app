package com.tubes.fittrack.api

data class ResponseRiwayat(
    val status: Boolean,
    val data: DatamaR
)

data class DatamaR(
    val makanan: List<Food>,
    val aktivitas: List<Activity>
)

data class FoodR(
    val name: String,
    val takaran: String,
    val kalori: String
)

data class ActivityR(
    val name: String,
    val durasi: Int,
    val kalori: Int
)
