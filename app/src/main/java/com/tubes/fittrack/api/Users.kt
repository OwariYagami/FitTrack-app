package com.tubes.fittrack.api

data class Users(
    val status: Boolean,
    val user: DataUser,
)

data class DataUser(
    val name: String,
    val email: String,
)
