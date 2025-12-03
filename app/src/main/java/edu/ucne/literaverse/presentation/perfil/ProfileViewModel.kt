package edu.ucne.literaverse.presentation.perfil

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.literaverse.data.local.TokenManager
import edu.ucne.literaverse.data.remote.Resource
import edu.ucne.literaverse.domain.usecase.profileUseCases.GetUserProfileUseCase
import edu.ucne.literaverse.domain.usecase.profileUseCases.GetUserSessionsUseCase
import edu.ucne.literaverse.domain.usecase.profileUseCases.LogoutAllSessionsUseCase
import edu.ucne.literaverse.domain.usecase.usuarioUseCases.LogoutUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val getUserSessionsUseCase: GetUserSessionsUseCase,
    private val logoutAllSessionsUseCase: LogoutAllSessionsUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileUiState())
    val state: StateFlow<ProfileUiState> = _state.asStateFlow()

    init {
        loadUserBasicInfo()
        onEvent(ProfileEvent.LoadProfile)
        onEvent(ProfileEvent.LoadSessions)
    }

    fun onEvent(event: ProfileEvent) {
        when (event) {
            is ProfileEvent.LoadProfile -> loadProfile()
            is ProfileEvent.LoadSessions -> loadSessions()
            is ProfileEvent.LogoutAllSessions -> showLogoutAllDialog()
            is ProfileEvent.Logout -> logout()
            is ProfileEvent.UserMessageShown -> clearMessage()
            is ProfileEvent.Refresh -> refresh()
        }
    }

    private fun loadUserBasicInfo() {
        val userName = tokenManager.getUserName() ?: "Usuario"
        val userId = tokenManager.getUserId()
        val loginDate = tokenManager.getLoginDate()

        _state.update {
            it.copy(
                userName = userName,
                userId = userId,
                loginDate = loginDate
            )
        }
    }

    private fun loadProfile() = viewModelScope.launch {
        _state.update { it.copy(isLoading = true) }

        val userId = tokenManager.getUserId()
        if (userId == -1) {
            _state.update {
                it.copy(
                    isLoading = false,
                    userMessage = "Error: No se encontró ID de usuario"
                )
            }
            return@launch
        }

        when (val result = getUserProfileUseCase(userId)) {
            is Resource.Success -> {
                _state.update {
                    it.copy(
                        isLoading = false,
                        userProfile = result.data
                    )
                }
            }
            is Resource.Error -> {
                _state.update {
                    it.copy(
                        isLoading = false,
                        userMessage = result.message
                    )
                }
            }
            is Resource.Loading -> {}
        }
    }

    private fun loadSessions() = viewModelScope.launch {
        _state.update { it.copy(isLoadingSessions = true) }

        val userId = tokenManager.getUserId()
        if (userId == -1) {
            _state.update {
                it.copy(
                    isLoadingSessions = false,
                    userMessage = "Error: No se encontró ID de usuario"
                )
            }
            return@launch
        }

        when (val result = getUserSessionsUseCase(userId)) {
            is Resource.Success -> {
                _state.update {
                    it.copy(
                        isLoadingSessions = false,
                        sessions = result.data ?: emptyList()
                    )
                }
            }
            is Resource.Error -> {
                _state.update {
                    it.copy(
                        isLoadingSessions = false,
                        userMessage = result.message
                    )
                }
            }
            is Resource.Loading -> {}
        }
    }

    private fun showLogoutAllDialog() {
        _state.update { it.copy(showLogoutAllDialog = true) }
    }

    fun dismissLogoutAllDialog() {
        _state.update { it.copy(showLogoutAllDialog = false) }
    }

    fun confirmLogoutAll() = viewModelScope.launch {
        _state.update {
            it.copy(
                showLogoutAllDialog = false,
                isLoading = true
            )
        }

        val userId = tokenManager.getUserId()
        if (userId == -1) {
            _state.update {
                it.copy(
                    isLoading = false,
                    userMessage = "Error: No se encontró ID de usuario"
                )
            }
            return@launch
        }

        when (val result = logoutAllSessionsUseCase(userId)) {
            is Resource.Success -> {
                tokenManager.clearSession()
                _state.update {
                    it.copy(
                        isLoading = false,
                        isLoggingOut = true,
                        userMessage = "Todas las sesiones han sido cerradas"
                    )
                }
            }
            is Resource.Error -> {
                _state.update {
                    it.copy(
                        isLoading = false,
                        userMessage = result.message
                    )
                }
            }
            is Resource.Loading -> {}
        }
    }

    private fun logout() = viewModelScope.launch {
        _state.update { it.copy(isLoading = true) }

        val token = tokenManager.getToken()
        if (token == null) {
            tokenManager.clearSession()
            _state.update {
                it.copy(
                    isLoading = false,
                    isLoggingOut = true
                )
            }
            return@launch
        }

        when (val result = logoutUseCase(token)) {
            is Resource.Success -> {
                tokenManager.clearSession()
                _state.update {
                    it.copy(
                        isLoading = false,
                        isLoggingOut = true,
                        userMessage = "Sesión cerrada exitosamente"
                    )
                }
            }
            is Resource.Error -> {
                tokenManager.clearSession()
                _state.update {
                    it.copy(
                        isLoading = false,
                        isLoggingOut = true,
                        userMessage = result.message
                    )
                }
            }
            is Resource.Loading -> {}
        }
    }

    private fun refresh() {
        loadProfile()
        loadSessions()
    }

    private fun clearMessage() {
        _state.update { it.copy(userMessage = null) }
    }
}