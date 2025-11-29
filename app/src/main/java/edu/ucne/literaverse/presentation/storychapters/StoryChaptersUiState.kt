package edu.ucne.literaverse.presentation.storychapters

import edu.ucne.literaverse.domain.model.Chapter
import edu.ucne.literaverse.domain.model.StoryDetail

data class StoryChaptersUiState(
    val story: StoryDetail? = null,
    val chapters: List<Chapter> = emptyList(),
    val selectedTab: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null
)