package edu.ucne.literaverse.data.remote.dto

data class LoginResponse(
    val userId: Int,
    val userName: String,
    val token: String,
    val loginDate: String
)