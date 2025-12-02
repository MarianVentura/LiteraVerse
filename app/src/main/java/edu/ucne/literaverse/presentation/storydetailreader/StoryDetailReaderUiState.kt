package edu.ucne.literaverse.presentation.storydetailreader

import edu.ucne.literaverse.domain.model.StoryReader

data class StoryDetailReaderUiState(
    val story: StoryReader? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val userMessage: String? = null,
    val isFavorite: Boolean = false,
    val isInLibrary: Boolean = false
)