package edu.ucne.literaverse.data.remote.dto

data class SessionResponse(
    val sessionId: Int,
    val userId: Int,
    val token: String,
    val createdAt: String,
    val lastActivity: String?,
    val isActive: Boolean,
    val deviceInfo: String?
)