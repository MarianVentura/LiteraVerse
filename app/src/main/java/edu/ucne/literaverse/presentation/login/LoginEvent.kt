package edu.ucne.literaverse.presentation.login

sealed interface LoginEvent {
    data class UserNameChanged(val userName: String) : LoginEvent
    data class PasswordChanged(val password: String) : LoginEvent
    object Login : LoginEvent
    object UserMessageShown : LoginEvent
}
