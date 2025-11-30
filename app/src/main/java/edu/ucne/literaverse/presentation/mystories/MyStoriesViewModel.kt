package edu.ucne.literaverse.presentation.mystories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.literaverse.data.local.TokenManager
import edu.ucne.literaverse.data.remote.Resource
import edu.ucne.literaverse.domain.usecase.storyUseCases.DeleteStoryUseCase
import edu.ucne.literaverse.domain.usecase.storyUseCases.GetMyStoriesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyStoriesViewModel @Inject constructor(
    private val getMyStoriesUseCase: GetMyStoriesUseCase,
    private val deleteStoryUseCase: DeleteStoryUseCase,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _state = MutableStateFlow(MyStoriesUiState())
    val state: StateFlow<MyStoriesUiState> = _state.asStateFlow()

    init {
        loadStories()
    }

    fun onEvent(event: MyStoriesEvent) {
        when (event) {
            is MyStoriesEvent.LoadStories -> loadStories()
            is MyStoriesEvent.OnFilterChanged -> applyFilter(event.filter)
            is MyStoriesEvent.OnDeleteStory -> deleteStory(event.storyId)
            else -> {}
        }
    }

    private fun loadStories() = viewModelScope.launch {
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

        try {
            getMyStoriesUseCase(userId).collect { stories ->
                _state.update {
                    it.copy(
                        stories = stories,
                        filteredStories = filterStories(stories, it.selectedFilter),
                        isLoading = false,
                        error = null
                    )
                }
            }
        } catch (e: Exception) {
            _state.update {
                it.copy(
                    isLoading = false,
                    error = e.localizedMessage ?: "Error al cargar historias"
                )
            }
        }
    }

    private fun applyFilter(filter: StoryFilter) {
        _state.update {
            it.copy(
                selectedFilter = filter,
                filteredStories = filterStories(it.stories, filter)
            )
        }
    }

    private fun filterStories(stories: List<edu.ucne.literaverse.domain.model.StoryDetail>, filter: StoryFilter): List<edu.ucne.literaverse.domain.model.StoryDetail> {
        return when (filter) {
            StoryFilter.ALL -> stories
            StoryFilter.DRAFTS -> stories.filter { it.isDraft && !it.isPublished }
            StoryFilter.PUBLISHED -> stories.filter { it.isPublished }
        }
    }

    private fun deleteStory(storyId: Int) = viewModelScope.launch {
        _state.update { it.copy(isLoading = true) }

        when (val result = deleteStoryUseCase(storyId)) {
            is Resource.Success -> {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = null
                    )
                }
                loadStories()
            }
            is Resource.Error -> {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = result.message ?: "Error al eliminar historia"
                    )
                }
            }
            is Resource.Loading -> {}
        }
    }
}