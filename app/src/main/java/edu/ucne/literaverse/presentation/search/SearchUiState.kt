package edu.ucne.literaverse.presentation.search

import edu.ucne.literaverse.domain.model.EstadoNovel
import edu.ucne.literaverse.domain.model.Novel
import edu.ucne.literaverse.domain.model.OrdenCriterio

data class SearchUiState(
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val novels: List<Novel> = emptyList(),
    val selectedGenre: String? = null,
    val selectedCategory: String? = null,
    val selectedEstado: EstadoNovel? = null,
    val selectedOrden: OrdenCriterio = OrdenCriterio.RELEVANCIA,
    val showFilters: Boolean = false,
    val userMessage: String? = null,
    val hasSearched: Boolean = false
)