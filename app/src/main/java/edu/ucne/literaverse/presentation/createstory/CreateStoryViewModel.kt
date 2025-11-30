package edu.ucne.literaverse.presentation.createstory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.literaverse.data.local.TokenManager
import edu.ucne.literaverse.data.remote.Resource
import edu.ucne.literaverse.domain.usecase.storyUseCases.CreateStoryUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateStoryViewModel @Inject constructor(
    private val createStoryUseCase: CreateStoryUseCase,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _state = MutableStateFlow(CreateStoryUiState())
    val state: StateFlow<CreateStoryUiState> = _state.asStateFlow()

    fun onEvent(event: CreateStoryEvent) {
        when (event) {
            is CreateStoryEvent.OnTitleChanged -> {
                _state.update {
                    it.copy(
                        title = event.title,
                        titleError = null
                    )
                }
            }
            is CreateStoryEvent.OnSynopsisChanged -> {
                _state.update {
                    it.copy(
                        synopsis = event.synopsis,
                        synopsisError = null
                    )
                }
            }
            is CreateStoryEvent.OnGenreChanged -> {
                _state.update {
                    it.copy(
                        genre = event.genre,
                        genreError = null
                    )
                }
            }
            is CreateStoryEvent.OnTagsChanged -> {
                _state.update {
                    it.copy(tags = event.tags)
                }
            }
            is CreateStoryEvent.OnSaveAsDraft -> saveStory(isDraft = true)
            is CreateStoryEvent.OnPublish -> saveStory(isDraft = false)
        }
    }

    private fun saveStory(isDraft: Boolean) = viewModelScope.launch {
        if (!validateFields()) {
            return@launch
        }

        _state.update { it.copy(isLoading = true, error = null) }

        val userId = tokenManager.getUserId()

        if (userId == -1) {
            _state.update {
                it.copy(
                    isLoading = false,
                    error = "Sesión no válida"
                )
            }
            return@launch
        }

        val currentState = _state.value

        when (val result = createStoryUseCase(
            userId = userId,
            title = currentState.title,
            synopsis = currentState.synopsis,
            genre = currentState.genre,
            tags = currentState.tags.ifBlank { null }
        )) {
            is Resource.Success -> {
                _state.update {
                    it.copy(
                        isLoading = false,
                        success = true,
                        error = null
                    )
                }
            }
            is Resource.Error -> {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = result.message ?: "Error al crear la historia"
                    )
                }
            }
            is Resource.Loading -> {}
        }
    }

    private fun validateFields(): Boolean {
        val currentState = _state.value
        var isValid = true

        if (currentState.title.isBlank()) {
            _state.update {
                it.copy(titleError = "El título es obligatorio")
            }
            isValid = false
        } else if (currentState.title.length > 200) {
            _state.update {
                it.copy(titleError = "El título no puede exceder 200 caracteres")
            }
            isValid = false
        }

        if (currentState.synopsis.isBlank()) {
            _state.update {
                it.copy(synopsisError = "La sinopsis es obligatoria")
            }
            isValid = false
        } else if (currentState.synopsis.length > 1000) {
            _state.update {
                it.copy(synopsisError = "La sinopsis no puede exceder 1000 caracteres")
            }
            isValid = false
        }

        if (currentState.genre.isBlank()) {
            _state.update {
                it.copy(genreError = "El género es obligatorio")
            }
            isValid = false
        }

        return isValid
    }
}