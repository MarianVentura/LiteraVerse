package edu.ucne.literaverse.data.remote.dto

data class LoginResponse(
    val usuarioId: Int,
    val userName: String,
    val password: String
)