package edu.ucne.literaverse.domain.repository

import edu.ucne.literaverse.data.remote.Resource
import edu.ucne.literaverse.domain.model.Session
import edu.ucne.literaverse.domain.model.UserProfile

interface ProfileRepository {
    suspend fun getUserProfile(userId: Int): Resource<UserProfile>
    suspend fun getUserSessions(userId: Int): Resource<List<Session>>
    suspend fun logoutAllSessions(userId: Int): Resource<Unit>
}