package edu.ucne.literaverse.data.mappers

import edu.ucne.literaverse.data.remote.dto.SessionResponse
import edu.ucne.literaverse.data.remote.dto.UserProfileResponse
import edu.ucne.literaverse.domain.model.Session
import edu.ucne.literaverse.domain.model.UserProfile

fun UserProfileResponse.toDomain(): UserProfile {
    return UserProfile(
        userId = userId,
        userName = userName,
        storiesCount = storiesCount,
        publishedStoriesCount = publishedStoriesCount,
        totalViews = totalViews,
        favoritesCount = favoritesCount
    )
}

fun SessionResponse.toDomain(): Session {
    return Session(
        sessionId = sessionId,
        userId = userId,
        token = token,
        createdAt = createdAt,
        lastActivity = lastActivity,
        isActive = isActive,
        deviceInfo = deviceInfo
    )
}

fun List<SessionResponse>.toDomainList(): List<Session> {
    return this.map { it.toDomain() }
}