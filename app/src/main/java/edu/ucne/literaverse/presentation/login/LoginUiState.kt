package edu.ucne.literaverse.presentation.login

data class LoginUiState(
    val userName: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val userMessage: String? = null,
    val isLoginSuccessful: Boolean = false
)