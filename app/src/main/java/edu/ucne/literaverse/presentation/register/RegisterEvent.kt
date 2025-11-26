package edu.ucne.literaverse.presentation.register

sealed interface RegisterEvent {
    data class UserNameChanged(val userName: String) : RegisterEvent
    data class PasswordChanged(val password: String) : RegisterEvent
    data class ConfirmPasswordChanged(val confirmPassword: String) : RegisterEvent
    object Register : RegisterEvent
    object UserMessageShown : RegisterEvent
}