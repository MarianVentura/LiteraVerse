package edu.ucne.literaverse.presentation.chaptereditor

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.literaverse.data.remote.Resource
import edu.ucne.literaverse.domain.usecase.chapterUseCases.CreateChapterUseCase
import edu.ucne.literaverse.domain.usecase.chapterUseCases.GetChapterUseCase
import edu.ucne.literaverse.domain.usecase.chapterUseCases.GetStoryChaptersUseCase
import edu.ucne.literaverse.domain.usecase.chapterUseCases.UpdateChapterUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChapterEditorViewModel @Inject constructor(
    private val createChapterUseCase: CreateChapterUseCase,
    private val getChapterUseCase: GetChapterUseCase,
    private val updateChapterUseCase: UpdateChapterUseCase,
    private val getStoryChaptersUseCase: GetStoryChaptersUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val storyId: Int = savedStateHandle.get<Int>("storyId") ?: 0
    private val chapterId: Int? = savedStateHandle.get<Int>("chapterId")

    private val _state = MutableStateFlow(ChapterEditorUiState())
    val state: StateFlow<ChapterEditorUiState> = _state.asStateFlow()

    init {
        if (chapterId != null && chapterId > 0) {
            loadChapter(storyId, chapterId)
        } else {
            loadNextChapterNumber(storyId)
        }
    }

    fun onEvent(event: ChapterEditorEvent) {
        when (event) {
            is ChapterEditorEvent.OnTitleChanged -> {
                _state.update {
                    it.copy(
                        title = event.title,
                        titleError = null
                    )
                }
            }
            is ChapterEditorEvent.OnContentChanged -> {
                _state.update {
                    it.copy(
                        content = event.content,
                        contentError = null
                    )
                }
            }
            is ChapterEditorEvent.OnSaveAsDraft -> saveChapter(isDraft = true)
            is ChapterEditorEvent.OnPublish -> saveChapter(isDraft = false)
        }
    }

    private fun loadChapter(storyId: Int, chapterId: Int) = viewModelScope.launch {
        _state.update { it.copy(isLoading = true, isEditMode = true) }

        when (val result = getChapterUseCase(storyId, chapterId)) {
            is Resource.Success -> {
                val chapter = result.data
                if (chapter != null) {
                    _state.update {
                        it.copy(
                            title = chapter.title,
                            content = chapter.content,
                            chapterNumber = chapter.chapterNumber,
                            isLoading = false,
                            error = null
                        )
                    }
                } else {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = "Capítulo no encontrado"
                        )
                    }
                }
            }
            is Resource.Error -> {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = result.message ?: "Error al cargar capítulo"
                    )
                }
            }
            is Resource.Loading -> {}
        }
    }

    private fun loadNextChapterNumber(storyId: Int) = viewModelScope.launch {
        try {
            getStoryChaptersUseCase(storyId).collect { chapters ->
                val nextNumber = if (chapters.isEmpty()) 1 else chapters.maxOf { it.chapterNumber } + 1
                _state.update {
                    it.copy(chapterNumber = nextNumber)
                }
            }
        } catch (e: Exception) {
            _state.update {
                it.copy(chapterNumber = 1)
            }
        }
    }

    private fun saveChapter(isDraft: Boolean) = viewModelScope.launch {
        if (!validateFields()) {
            return@launch
        }

        _state.update { it.copy(isLoading = true, error = null) }

        val currentState = _state.value

        val result = if (currentState.isEditMode && chapterId != null) {
            updateChapterUseCase(
                storyId = storyId,
                chapterId = chapterId,
                title = currentState.title,
                content = currentState.content,
                isDraft = isDraft
            )
        } else {
            createChapterUseCase(
                storyId = storyId,
                title = currentState.title,
                content = currentState.content,
                chapterNumber = currentState.chapterNumber,
                isDraft = isDraft
            )
        }

        when (result) {
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
                        error = result.message ?: "Error al guardar capítulo"
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
        }

        if (currentState.content.isBlank()) {
            _state.update {
                it.copy(contentError = "El contenido es obligatorio")
            }
            isValid = false
        }

        return isValid
    }
}