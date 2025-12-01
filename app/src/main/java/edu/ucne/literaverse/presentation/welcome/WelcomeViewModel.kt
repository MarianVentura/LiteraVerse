package edu.ucne.literaverse.presentation.welcome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.literaverse.data.local.TokenManager
import edu.ucne.literaverse.data.remote.Resource
import edu.ucne.literaverse.domain.usecase.usuarioUseCases.ValidateTokenUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WelcomeViewModel @Inject constructor(
    private val validateTokenUseCase: ValidateTokenUseCase,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _state = MutableStateFlow(WelcomeUiState())
    val state: StateFlow<WelcomeUiState> = _state.asStateFlow()

    init {
        validateSession()
    }

    private fun validateSession() = viewModelScope.launch {
        _state.update { it.copy(isLoading = true) }

        val token = tokenManager.getToken()

        if (token == null) {
            _state.update {
                it.copy(
                    isLoading = false,
                    hasValidSession = false
                )
            }
            return@launch
        }

        when (val result = validateTokenUseCase(token)) {
            is Resource.Success -> {
                _state.update {
                    it.copy(
                        isLoading = false,
                        hasValidSession = true
                    )
                }
            }
            is Resource.Error -> {
                tokenManager.clearSession()
                _state.update {
                    it.copy(
                        isLoading = false,
                        hasValidSession = false
                    )
                }
            }
            is Resource.Loading -> {}
        }
    }
}

