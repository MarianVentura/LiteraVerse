package edu.ucne.literaverse.presentation.createstory

data class CreateStoryUiState(
    val title: String = "",
    val synopsis: String = "",
    val genre: String = "",
    val tags: String = "",
    val titleError: String? = null,
    val synopsisError: String? = null,
    val genreError: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)