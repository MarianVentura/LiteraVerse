package edu.ucne.literaverse.presentation.write

import edu.ucne.literaverse.domain.model.StoryDetail

data class WriterPanelUiState(
    val userName: String = "",
    val recentStory: StoryDetail? = null,
    val recentStoryPublishedParts: Int = 0,
    val recentStoryDraftParts: Int = 0,
    val myStoriesCount: Int = 0,
    val draftsCount: Int = 0,
    val publishedCount: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null
)