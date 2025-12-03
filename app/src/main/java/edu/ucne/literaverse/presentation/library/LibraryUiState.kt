package edu.ucne.literaverse.presentation.library

import edu.ucne.literaverse.domain.model.StoryWithProgress

data class LibraryUiState(
    val isLoading: Boolean = false,
    val selectedTab: LibraryTab = LibraryTab.FAVORITES,
    val favorites: List<StoryWithProgress> = emptyList(),
    val reading: List<StoryWithProgress> = emptyList(),
    val completed: List<StoryWithProgress> = emptyList(),
    val errorMessage: String? = null,
    val successMessage: String? = null
)

enum class LibraryTab {
    FAVORITES,
    READING,
    COMPLETED
}