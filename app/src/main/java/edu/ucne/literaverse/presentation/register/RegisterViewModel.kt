package edu.ucne.literaverse.presentation.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.literaverse.data.remote.Resource
import edu.ucne.literaverse.domain.usecase.usuarioUseCases.RegisterUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(RegisterUiState())
    val state: StateFlow<RegisterUiState> = _state.asStateFlow()

    fun onEvent(event: RegisterEvent) {
        when (event) {
            is RegisterEvent.UserNameChanged -> {
                _state.update { it.copy(userName = event.userName, userMessage = null) }
            }
            is RegisterEvent.PasswordChanged -> {
                _state.update { it.copy(password = event.password, userMessage = null) }
            }
            is RegisterEvent.ConfirmPasswordChanged -> {
                _state.update { it.copy(confirmPassword = event.confirmPassword, userMessage = null) }
            }
            is RegisterEvent.Register -> register()
            is RegisterEvent.UserMessageShown -> clearMessage()
        }
    }

    private fun register() = viewModelScope.launch {
        _state.update { it.copy(isLoading = true) }

        when (val result = registerUseCase(
            _state.value.userName,
            _state.value.password,
            _state.value.confirmPassword
        )) {
            is Resource.Success -> {
                _state.update {
                    it.copy(
                        isLoading = false,
                        isRegisterSuccessful = true,
                        userMessage = "Usuario registrado exitosamente"
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

    private fun clearMessage() {
        _state.update { it.copy(userMessage = null) }
    }
}