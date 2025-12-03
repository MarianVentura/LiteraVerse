package edu.ucne.literaverse.presentation.perfil

sealed interface ProfileEvent {
    data object LoadProfile : ProfileEvent
    data object LoadSessions : ProfileEvent
    data object LogoutAllSessions : ProfileEvent
    data object Logout : ProfileEvent
    data object UserMessageShown : ProfileEvent
    data object Refresh : ProfileEvent
}