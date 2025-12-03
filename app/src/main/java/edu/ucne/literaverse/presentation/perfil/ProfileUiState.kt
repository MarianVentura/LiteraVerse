package edu.ucne.literaverse.presentation.perfil

import edu.ucne.literaverse.domain.model.Session
import edu.ucne.literaverse.domain.model.UserProfile

data class ProfileUiState(
    val isLoading: Boolean = false,
    val isLoadingSessions: Boolean = false,
    val userProfile: UserProfile? = null,
    val sessions: List<Session> = emptyList(),
    val userName: String = "",
    val userId: Int = -1,
    val loginDate: String? = null,
    val userMessage: String? = null,
    val isLoggingOut: Boolean = false,
    val showLogoutAllDialog: Boolean = false
)