package edu.ucne.literaverse.presentation.search


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.literaverse.data.remote.Resource
import edu.ucne.literaverse.domain.model.SearchFilters
import edu.ucne.literaverse.domain.model.SortCriteria
import edu.ucne.literaverse.domain.usecase.searchUseCases.GetPopularStoriesUseCase
import edu.ucne.literaverse.domain.usecase.searchUseCases.SearchStoriesUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchStoriesUseCase: SearchStoriesUseCase,
    private val getPopularStoriesUseCase: GetPopularStoriesUseCase
) : ViewModel() {


    private val _state = MutableStateFlow(SearchUiState())
    val state = _state.asStateFlow()


    private var searchJob: Job? = null


    init {
        loadPopularStories()
    }


    fun onEvent(event: SearchEvent) {
        when (event) {
            is SearchEvent.OnQueryChange -> {
                _state.update { it.copy(searchQuery = event.query) }
                searchJob?.cancel()
                searchJob = viewModelScope.launch {
                    delay(300)
                    performSearch()
                }
            }
            is SearchEvent.OnGenreSelect -> {
                _state.update { it.copy(selectedGenre = event.genre) }
                performSearch()
            }
            is SearchEvent.OnStatusSelect -> {
                _state.update { it.copy(selectedStatus = event.status) }
                performSearch()
            }
            is SearchEvent.OnSortChange -> {
                _state.update { it.copy(selectedSort = event.sortBy) }
                performSearch()
            }
            SearchEvent.OnSearch -> performSearch()
            SearchEvent.ClearFilters -> {
                _state.update {
                    it.copy(
                        searchQuery = "",
                        selectedGenre = null,
                        selectedStatus = null,
                        selectedSort = SortCriteria.RELEVANCE
                    )
                }
                loadPopularStories()
            }
            SearchEvent.ToggleFilters -> {
                _state.update { it.copy(showFilters = !it.showFilters) }
            }
            is SearchEvent.OnStoryClick -> {
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
            currentState.selectedStatus == null
        ) {
            loadPopularStories()
            return@launch
        }


        _state.update { it.copy(isLoading = true) }


        val filters = SearchFilters(
            query = currentState.searchQuery,
            genre = currentState.selectedGenre,
            status = currentState.selectedStatus,
            sortBy = currentState.selectedSort
        )


        when (val result = searchStoriesUseCase(filters)) {
            is Resource.Success -> {
                _state.update {
                    it.copy(
                        isLoading = false,
                        stories = result.data ?: emptyList(),
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


    private fun loadPopularStories() = viewModelScope.launch {
        _state.update { it.copy(isLoading = true) }


        when (val result = getPopularStoriesUseCase()) {
            is Resource.Success -> {
                _state.update {
                    it.copy(
                        isLoading = false,
                        stories = result.data ?: emptyList(),
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


