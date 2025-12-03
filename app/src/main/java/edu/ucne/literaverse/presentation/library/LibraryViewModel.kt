package edu.ucne.literaverse.presentation.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.literaverse.data.local.TokenManager
import edu.ucne.literaverse.data.remote.Resource
import edu.ucne.literaverse.domain.usecase.libraryUseCases.AddFavoriteUseCase
import edu.ucne.literaverse.domain.usecase.libraryUseCases.GetCompletedStoriesUseCase
import edu.ucne.literaverse.domain.usecase.libraryUseCases.GetFavoritesUseCase
import edu.ucne.literaverse.domain.usecase.libraryUseCases.GetReadingStoriesUseCase
import edu.ucne.literaverse.domain.usecase.libraryUseCases.MarkAsCompletedUseCase
import edu.ucne.literaverse.domain.usecase.libraryUseCases.RemoveFavoriteUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val getFavoritesUseCase: GetFavoritesUseCase,
    private val addFavoriteUseCase: AddFavoriteUseCase,
    private val removeFavoriteUseCase: RemoveFavoriteUseCase,
    private val getReadingStoriesUseCase: GetReadingStoriesUseCase,
    private val getCompletedStoriesUseCase: GetCompletedStoriesUseCase,
    private val markAsCompletedUseCase: MarkAsCompletedUseCase,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(LibraryUiState())
    val uiState = _uiState.asStateFlow()

    init {
        onEvent(LibraryEvent.LoadLibrary)
    }

    fun onEvent(event: LibraryEvent) {
        when (event) {
            is LibraryEvent.LoadLibrary -> loadLibrary()
            is LibraryEvent.SelectTab -> selectTab(event.tab)
            is LibraryEvent.RemoveFromFavorites -> removeFromFavorites(event.storyId)
            is LibraryEvent.AddToFavorites -> addToFavorites(event.storyId)
            is LibraryEvent.MarkAsCompleted -> markAsCompleted(event.storyId)
            is LibraryEvent.OnStoryClick -> {}
            is LibraryEvent.Refresh -> refresh()
            is LibraryEvent.UserMessageShown -> clearMessages()
        }
    }

    private fun loadLibrary() {
        val userId = tokenManager.getUserId()
        if (userId == -1) {
            _uiState.update { it.copy(errorMessage = "Usuario no autenticado") }
            return
        }

        viewModelScope.launch {
            getFavoritesUseCase(userId).collect { favorites ->
                _uiState.update { it.copy(favorites = favorites) }
            }
        }

        viewModelScope.launch {
            getReadingStoriesUseCase(userId).collect { reading ->
                _uiState.update { it.copy(reading = reading) }
            }
        }

        viewModelScope.launch {
            getCompletedStoriesUseCase(userId).collect { completed ->
                _uiState.update { it.copy(completed = completed) }
            }
        }
    }

    private fun selectTab(tab: LibraryTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    private fun removeFromFavorites(storyId: Int) {
        val userId = tokenManager.getUserId()
        if (userId == -1) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = removeFavoriteUseCase(userId, storyId)) {
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            successMessage = "Eliminado de favoritos"
                        )
                    }
                }
                is Resource.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.message ?: "Error al eliminar favorito"
                        )
                    }
                }
                is Resource.Loading -> {}
            }
        }
    }

    private fun addToFavorites(storyId: Int) {
        val userId = tokenManager.getUserId()
        if (userId == -1) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = addFavoriteUseCase(userId, storyId)) {
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            successMessage = "Agregado a favoritos"
                        )
                    }
                }
                is Resource.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.message ?: "Error al agregar favorito"
                        )
                    }
                }
                is Resource.Loading -> {}
            }
        }
    }

    private fun markAsCompleted(storyId: Int) {
        val userId = tokenManager.getUserId()
        if (userId == -1) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = markAsCompletedUseCase(userId, storyId)) {
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            successMessage = "Historia marcada como completada"
                        )
                    }
                }
                is Resource.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.message ?: "Error al marcar como completada"
                        )
                    }
                }
                is Resource.Loading -> {}
            }
        }
    }

    private fun refresh() {
        loadLibrary()
    }

    private fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }
}