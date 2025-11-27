package edu.ucne.literaverse.presentation.home

import edu.ucne.literaverse.domain.model.Genre
import edu.ucne.literaverse.domain.model.Story

data class HomeUiState(
    val featured: List<Story> = emptyList(),
    val popular: List<Story> = emptyList(),
    val recent: List<Story> = emptyList(),
    val genres: List<Genre> = emptyList(),
    val storiesByGenre: List<Story> = emptyList(),
    val selectedGenre: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)