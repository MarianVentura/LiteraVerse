package edu.ucne.literaverse.presentation.chapterreader

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.literaverse.data.local.TokenManager
import edu.ucne.literaverse.data.remote.Resource
import edu.ucne.literaverse.domain.usecase.chapterUseCases.GetChapterUseCase
import edu.ucne.literaverse.domain.usecase.chapterUseCases.GetNextChapterUseCase
import edu.ucne.literaverse.domain.usecase.chapterUseCases.GetPreviousChapterUseCase
import edu.ucne.literaverse.domain.usecase.chapterUseCases.GetStoryChaptersUseCase
import edu.ucne.literaverse.domain.usecase.chapterUseCases.SaveReadingProgressUseCase
import edu.ucne.literaverse.domain.usecase.storyUseCases.GetStoryDetailUseCase
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class ChapterReaderViewModel @Inject constructor(
    private val getChapterUseCase: GetChapterUseCase,
    private val getStoryDetailUseCase: GetStoryDetailUseCase,
    private val getStoryChaptersUseCase: GetStoryChaptersUseCase,
    private val getNextChapterUseCase: GetNextChapterUseCase,
    private val getPreviousChapterUseCase: GetPreviousChapterUseCase,
    private val saveReadingProgressUseCase: SaveReadingProgressUseCase,
    private val tokenManager: TokenManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val storyId: Int = savedStateHandle["storyId"] ?: 0
    private val chapterId: Int = savedStateHandle["chapterId"] ?: 0

    private val _state = MutableStateFlow(ChapterReaderUiState(isLoading = true))
    val state: StateFlow<ChapterReaderUiState> = _state.asStateFlow()

    private val scrollPositionFlow = MutableStateFlow(0.0)

    init {
        loadChapter(storyId, chapterId)
        loadStory(storyId)
        observeScrollPosition()
    }

    fun onEvent(event: ChapterReaderEvent) {
        when (event) {
            is ChapterReaderEvent.LoadChapter -> loadChapter(event.storyId, event.chapterId)
            is ChapterReaderEvent.OnScrollPositionChanged -> updateScrollPosition(event.position)
            is ChapterReaderEvent.OnNextChapter -> navigateToNextChapter()
            is ChapterReaderEvent.OnPreviousChapter -> navigateToPreviousChapter()
            is ChapterReaderEvent.OnToggleMenu -> toggleMenu()
            is ChapterReaderEvent.SaveProgress -> saveProgress()
            is ChapterReaderEvent.UserMessageShown -> clearMessage()
        }
    }

    private fun loadChapter(storyId: Int, chapterId: Int) = viewModelScope.launch {
        _state.update { it.copy(isLoading = true, error = null) }

        val result = getChapterUseCase(storyId, chapterId)
        if (result is Resource.Success) {
            val chapter = result.data
            if (chapter != null) {
                _state.update {
                    it.copy(
                        chapter = chapter,
                        currentChapterNumber = chapter.chapterNumber,
                        isLoading = false,
                        error = null
                    )
                }
                checkNavigationAvailability(storyId, chapter.chapterNumber)
            } else {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Capítulo no encontrado"
                    )
                }
            }
        } else if (result is Resource.Error) {
            _state.update {
                it.copy(
                    isLoading = false,
                    error = result.message ?: "Error al cargar capítulo"
                )
            }
        }
    }

    private fun loadStory(storyId: Int) = viewModelScope.launch {
        val result = getStoryDetailUseCase(storyId)
        if (result is Resource.Success) {
            val story = result.data
            _state.update {
                it.copy(
                    story = story,
                    totalChapters = story?.chapters?.size ?: 0
                )
            }
        }
    }

    private fun checkNavigationAvailability(storyId: Int, currentChapterNumber: Int) = viewModelScope.launch {
        val nextResult = getNextChapterUseCase(storyId, currentChapterNumber)
        val previousResult = getPreviousChapterUseCase(storyId, currentChapterNumber)

        _state.update {
            it.copy(
                hasNextChapter = nextResult is Resource.Success && nextResult.data != null,
                hasPreviousChapter = previousResult is Resource.Success && previousResult.data != null
            )
        }
    }

    private fun navigateToNextChapter() = viewModelScope.launch {
        val currentChapterNumber = _state.value.currentChapterNumber

        val result = getNextChapterUseCase(storyId, currentChapterNumber)
        if (result is Resource.Success) {
            val nextChapter = result.data
            if (nextChapter != null) {
                saveProgress()
                loadChapter(storyId, nextChapter.chapterId)
            } else {
                _state.update {
                    it.copy(userMessage = "No hay más capítulos")
                }
            }
        } else if (result is Resource.Error) {
            _state.update {
                it.copy(userMessage = result.message ?: "Error al cargar siguiente capítulo")
            }
        }
    }

    private fun navigateToPreviousChapter() = viewModelScope.launch {
        val currentChapterNumber = _state.value.currentChapterNumber

        val result = getPreviousChapterUseCase(storyId, currentChapterNumber)
        if (result is Resource.Success) {
            val previousChapter = result.data
            if (previousChapter != null) {
                saveProgress()
                loadChapter(storyId, previousChapter.chapterId)
            } else {
                _state.update {
                    it.copy(userMessage = "Este es el primer capítulo")
                }
            }
        } else if (result is Resource.Error) {
            _state.update {
                it.copy(userMessage = result.message ?: "Error al cargar capítulo anterior")
            }
        }
    }

    private fun updateScrollPosition(position: Double) {
        _state.update { it.copy(scrollPosition = position) }
        scrollPositionFlow.value = position
    }

    private fun observeScrollPosition() = viewModelScope.launch {
        scrollPositionFlow
            .debounce(2000)
            .collect { position ->
                if (position > 0) {
                    saveProgress()
                }
            }
    }

    private fun saveProgress() = viewModelScope.launch {
        val userId = tokenManager.getUserId()
        val currentChapter = _state.value.chapter
        val scrollPosition = _state.value.scrollPosition

        if (userId != -1 && currentChapter != null) {
            saveReadingProgressUseCase(
                userId = userId,
                storyId = storyId,
                chapterId = currentChapter.chapterId,
                scrollPosition = scrollPosition
            )
        }
    }

    private fun toggleMenu() {
        _state.update { it.copy(showMenu = !it.showMenu) }
    }

    private fun clearMessage() {
        _state.update { it.copy(userMessage = null) }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.launch {
            saveProgress()
        }
    }
}
