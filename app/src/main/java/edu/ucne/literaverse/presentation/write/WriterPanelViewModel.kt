package edu.ucne.literaverse.presentation.write

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.literaverse.data.local.TokenManager
import edu.ucne.literaverse.domain.usecase.chapterUseCases.GetStoryChaptersUseCase
import edu.ucne.literaverse.domain.usecase.storyUseCases.GetMyStoriesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WriterPanelViewModel @Inject constructor(
    private val getMyStoriesUseCase: GetMyStoriesUseCase,
    private val getStoryChaptersUseCase: GetStoryChaptersUseCase,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _state = MutableStateFlow(WriterPanelUiState())
    val state: StateFlow<WriterPanelUiState> = _state.asStateFlow()

    init {
        loadWriterData()
    }

    fun onEvent(event: WriterPanelEvent) {
        when (event) {
            is WriterPanelEvent.LoadWriterData -> loadWriterData()
            else -> {}
        }
    }

    private fun loadWriterData() = viewModelScope.launch {
        _state.update { it.copy(isLoading = true, error = null) }

        val userId = tokenManager.getUserId()
        val userName = tokenManager.getUserName() ?: "Escritor"

        if (userId == -1) {
            _state.update {
                it.copy(
                    isLoading = false,
                    error = "Sesión no válida"
                )
            }
            return@launch
        }

        try {
            getMyStoriesUseCase(userId).collect { stories ->
                val recentStory = stories
                    .filter { it.isDraft || it.isPublished }
                    .maxByOrNull { it.updatedAt }

                val draftsCount = stories.count { it.isDraft && !it.isPublished }
                val publishedCount = stories.count { it.isPublished }

                _state.update {
                    it.copy(
                        userName = userName,
                        recentStory = recentStory,
                        myStoriesCount = stories.size,
                        draftsCount = draftsCount,
                        publishedCount = publishedCount,
                        isLoading = false,
                        error = null
                    )
                }

                if (recentStory != null) {
                    loadRecentStoryChapters(recentStory.storyId)
                }
            }
        } catch (e: Exception) {
            _state.update {
                it.copy(
                    isLoading = false,
                    error = e.localizedMessage ?: "Error al cargar datos"
                )
            }
        }
    }

    private fun loadRecentStoryChapters(storyId: Int) = viewModelScope.launch {
        try {
            getStoryChaptersUseCase(storyId).collect { chapters ->
                val publishedParts = chapters.count { it.isPublished }
                val draftParts = chapters.count { it.isDraft && !it.isPublished }

                _state.update {
                    it.copy(
                        recentStoryPublishedParts = publishedParts,
                        recentStoryDraftParts = draftParts
                    )
                }
            }
        } catch (e: Exception) {
            _state.update {
                it.copy(
                    recentStoryPublishedParts = 0,
                    recentStoryDraftParts = 0
                )
            }
        }
    }
}