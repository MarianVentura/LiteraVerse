package edu.ucne.literaverse.domain.model

data class Session(
    val sessionId: Int,
    val userId: Int,
    val token: String,
    val createdAt: String,
    val lastActivity: String?,
    val isActive: Boolean,
    val deviceInfo: String?
)