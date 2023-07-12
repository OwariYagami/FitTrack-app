package com.tubes.fittrack.api

data class ResponseTanggal(
    val status: Boolean,
    val data: List<Tanggal>,
)

data class Tanggal(
    val tanggal: String,
)
