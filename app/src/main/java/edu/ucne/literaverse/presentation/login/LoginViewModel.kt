package edu.ucne.literaverse.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.literaverse.data.local.TokenManager
import edu.ucne.literaverse.data.remote.Resource
import edu.ucne.literaverse.domain.usecase.usuarioUseCases.LoginUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _state = MutableStateFlow(LoginUiState())
    val state: StateFlow<LoginUiState> = _state.asStateFlow()

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.UserNameChanged -> {
                _state.update { it.copy(userName = event.userName, userMessage = null) }
            }
            is LoginEvent.PasswordChanged -> {
                _state.update { it.copy(password = event.password, userMessage = null) }
            }
            is LoginEvent.Login -> login()
            is LoginEvent.UserMessageShown -> clearMessage()
        }
    }

    private fun login() = viewModelScope.launch {
        _state.update { it.copy(isLoading = true) }

        when (val result = loginUseCase(_state.value.userName, _state.value.password)) {
            is Resource.Success -> {
                result.data?.let { usuario ->
                    tokenManager.saveToken(usuario.token)
                    tokenManager.saveUserId(usuario.usuarioId)
                    tokenManager.saveUserName(usuario.userName)
                    _state.update {
                        it.copy(
                            isLoading = false,
                            isLoginSuccessful = true,
                            userMessage = "Login exitoso"
                        )
                    }
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

    private fun clearMessage() {
        _state.update { it.copy(userMessage = null) }
    }
}