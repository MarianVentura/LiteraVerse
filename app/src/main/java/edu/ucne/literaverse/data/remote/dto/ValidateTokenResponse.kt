package edu.ucne.literaverse.data.remote.dto

data class ValidateTokenResponse(
    val isValid: Boolean,
    val userId: Int? = null,
    val message: String? = null
)