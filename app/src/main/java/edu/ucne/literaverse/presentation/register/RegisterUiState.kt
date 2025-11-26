package edu.ucne.literaverse.presentation.register

data class RegisterUiState(
    val userName: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val userMessage: String? = null,
    val isRegisterSuccessful: Boolean = false
)