package edu.ucne.literaverse.presentation.chaptereditor

data class ChapterEditorUiState(
    val title: String = "",
    val content: String = "",
    val chapterNumber: Int = 1,
    val titleError: String? = null,
    val contentError: String? = null,
    val isLoading: Boolean = false,
    val isEditMode: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)