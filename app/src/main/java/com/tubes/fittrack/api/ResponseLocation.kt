package com.tubes.fittrack.api

data class ResponseLocation(
    val status: Boolean,
    val data: ArrayList<DataLocation>
)

data class DataLocation(
    val name: String,
    val foto: String,
    val longitude: Double,
    val latitude: Double
)