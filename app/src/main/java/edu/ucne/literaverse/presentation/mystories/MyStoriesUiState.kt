package edu.ucne.literaverse.presentation.mystories

import edu.ucne.literaverse.domain.model.StoryDetail

data class MyStoriesUiState(
    val stories: List<StoryDetail> = emptyList(),
    val filteredStories: List<StoryDetail> = emptyList(),
    val selectedFilter: StoryFilter = StoryFilter.ALL,
    val isLoading: Boolean = false,
    val error: String? = null
)

enum class StoryFilter {
    ALL,
    DRAFTS,
    PUBLISHED
}