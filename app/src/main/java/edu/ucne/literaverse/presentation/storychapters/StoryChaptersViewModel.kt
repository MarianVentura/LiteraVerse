package edu.ucne.literaverse.presentation.storychapters

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.literaverse.data.remote.Resource
import edu.ucne.literaverse.domain.usecase.chapterUseCases.DeleteChapterUseCase
import edu.ucne.literaverse.domain.usecase.chapterUseCases.GetStoryChaptersUseCase
import edu.ucne.literaverse.domain.usecase.chapterUseCases.PublishChapterUseCase
import edu.ucne.literaverse.domain.usecase.storyUseCases.GetStoryDetailUseCase
import edu.ucne.literaverse.domain.usecase.storyUseCases.PublishStoryUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StoryChaptersViewModel @Inject constructor(
    private val getStoryDetailUseCase: GetStoryDetailUseCase,
    private val getStoryChaptersUseCase: GetStoryChaptersUseCase,
    private val deleteChapterUseCase: DeleteChapterUseCase,
    private val publishChapterUseCase: PublishChapterUseCase,
    private val publishStoryUseCase: PublishStoryUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val storyId: Int = savedStateHandle.get<Int>("storyId") ?: 0

    private val _state = MutableStateFlow(StoryChaptersUiState())
    val state: StateFlow<StoryChaptersUiState> = _state.asStateFlow()

    init {
        loadStory(storyId)
    }

    fun onEvent(event: StoryChaptersEvent) {
        when (event) {
            is StoryChaptersEvent.LoadStory -> loadStory(event.storyId)
            is StoryChaptersEvent.OnTabSelected -> {
                _state.update { it.copy(selectedTab = event.index) }
            }
            is StoryChaptersEvent.OnDeleteChapter -> deleteChapter(event.chapterId)
            is StoryChaptersEvent.OnPublishChapter -> publishChapter(event.chapterId)
            is StoryChaptersEvent.OnPublishStory -> publishStory()
            else -> {}
        }
    }

    private fun loadStory(storyId: Int) = viewModelScope.launch {
        _state.update { it.copy(isLoading = true, error = null) }

        try {
            when (val result = getStoryDetailUseCase(storyId)) {
                is Resource.Success -> {
                    val story = result.data
                    if (story != null) {
                        _state.update {
                            it.copy(
                                story = story,
                                isLoading = false,
                                error = null
                            )
                        }
                        loadChapters(storyId)
                    } else {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                error = "Historia no encontrada"
                            )
                        }
                    }
                }
                is Resource.Error -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = result.message ?: "Error al cargar historia"
                        )
                    }
                }
                is Resource.Loading -> {}
            }
        } catch (e: Exception) {
            _state.update {
                it.copy(
                    isLoading = false,
                    error = e.localizedMessage ?: "Error al cargar historia"
                )
            }
        }
    }

    private fun loadChapters(storyId: Int) = viewModelScope.launch {
        try {
            getStoryChaptersUseCase(storyId).collect { chapters ->
                _state.update {
                    it.copy(chapters = chapters)
                }
            }
        } catch (e: Exception) {
            _state.update {
                it.copy(error = e.localizedMessage ?: "Error al cargar capítulos")
            }
        }
    }

    private fun deleteChapter(chapterId: Int) = viewModelScope.launch {
        _state.update { it.copy(isLoading = true) }

        when (val result = deleteChapterUseCase(storyId = storyId, chapterId = chapterId)) {
            is Resource.Success -> {
                _state.update { it.copy(isLoading = false) }
                loadStory(storyId)
            }
            is Resource.Error -> {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = result.message ?: "Error al eliminar capítulo"
                    )
                }
            }
            is Resource.Loading -> {}
        }
    }

    private fun publishChapter(chapterId: Int) = viewModelScope.launch {
        _state.update { it.copy(isLoading = true) }

        when (val result = publishChapterUseCase(storyId = storyId, chapterId = chapterId)) {
            is Resource.Success -> {
                _state.update { it.copy(isLoading = false) }
                loadStory(storyId)
            }
            is Resource.Error -> {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = result.message ?: "Error al publicar capítulo"
                    )
                }
            }
            is Resource.Loading -> {}
        }
    }

    private fun publishStory() = viewModelScope.launch {
        _state.update { it.copy(isLoading = true) }

        when (val result = publishStoryUseCase(storyId)) {
            is Resource.Success -> {
                _state.update { it.copy(isLoading = false) }
                loadStory(storyId)
            }
            is Resource.Error -> {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = result.message ?: "Error al publicar historia"
                    )
                }
            }
            is Resource.Loading -> {}
        }
    }
}