package edu.ucne.literaverse.presentation.storydetailreader

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.literaverse.data.local.TokenManager
import edu.ucne.literaverse.data.remote.Resource
import edu.ucne.literaverse.domain.usecase.libraryUseCases.AddFavoriteUseCase
import edu.ucne.literaverse.domain.usecase.libraryUseCases.MarkAsCompletedUseCase
import edu.ucne.literaverse.domain.usecase.libraryUseCases.RemoveFavoriteUseCase
import edu.ucne.literaverse.domain.usecase.storyUseCases.GetStoryForReaderUseCase
import edu.ucne.literaverse.presentation.components.LibraryStates
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StoryDetailReaderViewModel @Inject constructor(
    private val getStoryForReaderUseCase: GetStoryForReaderUseCase,
    private val addFavoriteUseCase: AddFavoriteUseCase,
    private val removeFavoriteUseCase: RemoveFavoriteUseCase,
    private val markAsCompletedUseCase: MarkAsCompletedUseCase,
    private val tokenManager: TokenManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val storyId: Int = savedStateHandle.get<Int>("storyId") ?: 0

    private val _state = MutableStateFlow(StoryDetailReaderUiState())
    val state: StateFlow<StoryDetailReaderUiState> = _state.asStateFlow()

    init {
        loadStory(storyId)
    }

    fun onEvent(event: StoryDetailReaderEvent) {
        when (event) {
            is StoryDetailReaderEvent.LoadStory -> loadStory(event.storyId)
            is StoryDetailReaderEvent.OnChapterClick -> {}
            is StoryDetailReaderEvent.OnStartReading -> startReading()
            is StoryDetailReaderEvent.OnToggleFavorite -> toggleFavorite()
            is StoryDetailReaderEvent.OnAddToLibrary -> addToLibrary()
            is StoryDetailReaderEvent.ShowLibraryMenu -> showLibraryMenu()
            is StoryDetailReaderEvent.DismissLibraryMenu -> dismissLibraryMenu()
            is StoryDetailReaderEvent.UpdateLibraryStates -> updateLibraryStates(event.states)
            is StoryDetailReaderEvent.UserMessageShown -> clearMessage()
        }
    }

    private fun loadStory(storyId: Int) = viewModelScope.launch {
        _state.update { it.copy(isLoading = true, error = null) }

        when (val result = getStoryForReaderUseCase(storyId)) {
            is Resource.Success -> {
                _state.update {
                    it.copy(
                        story = result.data,
                        isLoading = false,
                        error = null
                    )
                }
            }
            is Resource.Error -> {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = result.message ?: "Error al cargar la historia"
                    )
                }
            }
            is Resource.Loading -> {}
        }
    }

    private fun startReading() {
        val firstChapter = _state.value.story?.publishedChapters?.firstOrNull()
        if (firstChapter != null) {
            _state.update {
                it.copy(userMessage = "Iniciando lectura del capítulo ${firstChapter.chapterNumber}")
            }
        }
    }

    private fun toggleFavorite() {
        val userId = tokenManager.getUserId()
        if (userId == -1) return

        viewModelScope.launch {
            val currentFavorite = _state.value.isFavorite

            if (currentFavorite) {
                when (val result = removeFavoriteUseCase(userId, storyId)) {
                    is Resource.Success -> {
                        _state.update {
                            it.copy(
                                isFavorite = false,
                                userMessage = "Eliminado de favoritos"
                            )
                        }
                    }
                    is Resource.Error -> {
                        _state.update {
                            it.copy(userMessage = result.message ?: "Error al eliminar favorito")
                        }
                    }
                    is Resource.Loading -> {}
                }
            } else {
                when (val result = addFavoriteUseCase(userId, storyId)) {
                    is Resource.Success -> {
                        _state.update {
                            it.copy(
                                isFavorite = true,
                                userMessage = "Agregado a favoritos"
                            )
                        }
                    }
                    is Resource.Error -> {
                        _state.update {
                            it.copy(userMessage = result.message ?: "Error al agregar favorito")
                        }
                    }
                    is Resource.Loading -> {}
                }
            }
        }
    }

    private fun showLibraryMenu() {
        _state.update { it.copy(showLibraryMenu = true) }
    }

    private fun dismissLibraryMenu() {
        _state.update { it.copy(showLibraryMenu = false) }
    }

    private fun updateLibraryStates(states: LibraryStates) {
        val userId = tokenManager.getUserId()
        if (userId == -1) return

        viewModelScope.launch {
            if (states.isFavorite != _state.value.isFavorite) {
                if (states.isFavorite) {
                    addFavoriteUseCase(userId, storyId)
                } else {
                    removeFavoriteUseCase(userId, storyId)
                }
            }

            if (states.isReading != _state.value.isReading) {
                _state.update { it.copy(isReading = states.isReading) }
            }

            if (states.isCompleted) {
                markAsCompletedUseCase(userId, storyId)
            }

            _state.update {
                it.copy(
                    isFavorite = states.isFavorite,
                    isReading = states.isReading,
                    isCompleted = states.isCompleted,
                    userMessage = "Biblioteca actualizada"
                )
            }
        }
    }
    private fun addToLibrary() {
        _state.update {
            it.copy(
                isInLibrary = !it.isInLibrary,
                userMessage = if (!it.isInLibrary) "Agregado a tu biblioteca" else "Eliminado de tu biblioteca"
            )
        }
    }

    private fun clearMessage() {
        _state.update { it.copy(userMessage = null) }
    }
}