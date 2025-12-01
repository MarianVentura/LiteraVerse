package edu.ucne.literaverse.presentation.search


import edu.ucne.literaverse.domain.model.SortCriteria
import edu.ucne.literaverse.domain.model.Story


data class SearchUiState(
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val stories: List<Story> = emptyList(),
    val selectedGenre: String? = null,
    val selectedStatus: String? = null,
    val selectedSort: SortCriteria = SortCriteria.RELEVANCE,
    val showFilters: Boolean = false,
    val userMessage: String? = null,
    val hasSearched: Boolean = false
)


