package edu.ucne.literaverse.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.literaverse.data.remote.Resource
import edu.ucne.literaverse.domain.model.SearchFilters
import edu.ucne.literaverse.domain.usecase.searchUseCases.GetPopularNovelsUseCase
import edu.ucne.literaverse.domain.usecase.searchUseCases.SearchNovelsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import edu.ucne.literaverse.domain.model.OrdenCriterio

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchNovelsUseCase: SearchNovelsUseCase,
    private val getPopularNovelsUseCase: GetPopularNovelsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(SearchUiState())
    val state = _state.asStateFlow()

    init {
        loadPopularNovels()
    }

    fun onEvent(event: SearchEvent) {
        when (event) {
            is SearchEvent.OnQueryChange -> {
                _state.update { it.copy(searchQuery = event.query) }
            }

            is SearchEvent.OnGenreSelect -> {
                _state.update { it.copy(selectedGenre = event.genero) }
                performSearch()
            }

            is SearchEvent.OnCategorySelect -> {
                _state.update { it.copy(selectedCategory = event.categoria) }
                performSearch()
            }

            is SearchEvent.OnEstadoSelect -> {
                _state.update { it.copy(selectedEstado = event.estado) }
                performSearch()
            }

            is SearchEvent.OnOrdenChange -> {
                _state.update { it.copy(selectedOrden = event.orden) }
                performSearch()
            }

            SearchEvent.OnSearch -> performSearch()

            SearchEvent.ClearFilters -> {
                _state.update {
                    it.copy(
                        searchQuery = "",
                        selectedGenre = null,
                        selectedCategory = null,
                        selectedEstado = null,
                        selectedOrden = OrdenCriterio.RELEVANCIA
                    )
                }
                loadPopularNovels()
            }

            SearchEvent.ToggleFilters -> {
                _state.update { it.copy(showFilters = !it.showFilters) }
            }

            is SearchEvent.OnNovelClick -> {
                // Navegar a detalle de novela
            }

            SearchEvent.UserMessageShown -> {
                _state.update { it.copy(userMessage = null) }
            }
        }
    }

    private fun performSearch() = viewModelScope.launch {
        val currentState = _state.value

        if (currentState.searchQuery.isBlank() &&
            currentState.selectedGenre == null &&
            currentState.selectedCategory == null &&
            currentState.selectedEstado == null
        ) {
            loadPopularNovels()
            return@launch
        }

        _state.update { it.copy(isLoading = true) }

        val filters = SearchFilters(
            query = currentState.searchQuery,
            genero = currentState.selectedGenre,
            categoria = currentState.selectedCategory,
            estado = currentState.selectedEstado,
            ordenarPor = currentState.selectedOrden
        )

        when (val result = searchNovelsUseCase(filters)) {
            is Resource.Success -> {
                _state.update {
                    it.copy(
                        isLoading = false,
                        novels = result.data ?: emptyList(),
                        hasSearched = true,
                        userMessage = if (result.data.isNullOrEmpty()) "No se encontraron resultados" else null
                    )
                }
            }

            is Resource.Error -> {
                _state.update {
                    it.copy(
                        isLoading = false,
                        userMessage = result.message
                    )
                }
            }

            is Resource.Loading -> {
                _state.update { it.copy(isLoading = true) }
            }
        }
    }

    private fun loadPopularNovels() = viewModelScope.launch {
        _state.update { it.copy(isLoading = true) }

        when (val result = getPopularNovelsUseCase()) {
            is Resource.Success -> {
                _state.update {
                    it.copy(
                        isLoading = false,
                        novels = result.data ?: emptyList(),
                        hasSearched = false
                    )
                }
            }

            is Resource.Error -> {
                _state.update {
                    it.copy(
                        isLoading = false,
                        userMessage = result.message
                    )
                }
            }

            is Resource.Loading -> {
                _state.update { it.copy(isLoading = true) }
            }
        }
    }
}