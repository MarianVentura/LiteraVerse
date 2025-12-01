package edu.ucne.literaverse.presentation.welcome

data class WelcomeUiState(
    val isLoading: Boolean = true,
    val hasValidSession: Boolean = false
)