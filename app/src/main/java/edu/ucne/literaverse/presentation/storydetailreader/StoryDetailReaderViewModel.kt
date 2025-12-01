package edu.ucne.literaverse.presentation.storydetailreader

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.literaverse.data.remote.Resource
import edu.ucne.literaverse.domain.usecase.storyUseCases.GetStoryForReaderUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StoryDetailReaderViewModel @Inject constructor(
    private val getStoryForReaderUseCase: GetStoryForReaderUseCase,
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
            is StoryDetailReaderEvent.OnToggleLike -> toggleLike()
            is StoryDetailReaderEvent.OnShare -> shareStory()
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
        _state.update {
            it.copy(
                isFavorite = !it.isFavorite,
                userMessage = if (!it.isFavorite) "Agregado a favoritos" else "Eliminado de favoritos"
            )
        }
    }

    private fun toggleLike() {
        _state.update {
            it.copy(
                hasLiked = !it.hasLiked,
                userMessage = if (!it.hasLiked) "Te gustó esta historia" else "Ya no te gusta esta historia"
            )
        }
    }

    private fun shareStory() {
        _state.update {
            it.copy(userMessage = "Compartir funcionalidad pendiente")
        }
    }

    private fun clearMessage() {
        _state.update { it.copy(userMessage = null) }
    }
}