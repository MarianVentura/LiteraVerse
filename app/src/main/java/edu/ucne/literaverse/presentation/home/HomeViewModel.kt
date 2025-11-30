package edu.ucne.literaverse.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.literaverse.data.remote.Resource
import edu.ucne.literaverse.data.local.TokenManager
import edu.ucne.literaverse.domain.usecase.homeUseCases.GetHomeDataUseCase
import edu.ucne.literaverse.domain.usecase.usuarioUseCases.LogoutUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getHomeDataUseCase: GetHomeDataUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    init {
        loadHomeData()
    }

    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.LoadHomeData -> loadHomeData()
            is HomeEvent.OnGenreSelected -> {
                if (_state.value.selectedGenre == event.genre) {
                    _state.update { it.copy(selectedGenre = null, storiesByGenre = emptyList()) }
                } else {
                    loadStoriesByGenre(event.genre)
                }
            }
            is HomeEvent.Refresh -> loadHomeData()
            is HomeEvent.Logout -> logout()
        }
    }

    private fun loadHomeData() = viewModelScope.launch {
        _state.update { it.copy(isLoading = true, error = null) }

        val featuredResult = getHomeDataUseCase.getFeatured()
        val popularResult = getHomeDataUseCase.getPopular()
        val newResult = getHomeDataUseCase.getNew()
        val genresResult = getHomeDataUseCase.getGenres()

        when {
            featuredResult is Resource.Error -> {
                _state.update { it.copy(isLoading = false, error = featuredResult.message) }
            }
            popularResult is Resource.Error -> {
                _state.update { it.copy(isLoading = false, error = popularResult.message) }
            }
            newResult is Resource.Error -> {
                _state.update { it.copy(isLoading = false, error = newResult.message) }
            }
            genresResult is Resource.Error -> {
                _state.update { it.copy(isLoading = false, error = genresResult.message) }
            }
            else -> {
                _state.update {
                    it.copy(
                        featured = (featuredResult as? Resource.Success)?.data ?: emptyList(),
                        popular = (popularResult as? Resource.Success)?.data ?: emptyList(),
                        recent = (newResult as? Resource.Success)?.data ?: emptyList(),
                        genres = (genresResult as? Resource.Success)?.data ?: emptyList(),
                        isLoading = false,
                        error = null
                    )
                }
            }
        }
    }

    private fun loadStoriesByGenre(genreName: String) = viewModelScope.launch {
        _state.update { it.copy(isLoading = true, selectedGenre = genreName) }

        when (val result = getHomeDataUseCase.getByGenre(genreName)) {
            is Resource.Success -> {
                _state.update {
                    it.copy(
                        storiesByGenre = result.data ?: emptyList(),
                        isLoading = false,
                        error = null
                    )
                }
            }
            is Resource.Error -> {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = result.message,
                        selectedGenre = null,
                        storiesByGenre = emptyList()
                    )
                }
            }
            is Resource.Loading -> {}
        }
    }

    private fun logout() = viewModelScope.launch {
        val token = tokenManager.getToken() ?: return@launch

        when (logoutUseCase(token)) {
            is Resource.Success -> {
                tokenManager.clearSession()
            }
            is Resource.Error -> {
                tokenManager.clearSession()
            }
            is Resource.Loading -> {}
        }
    }
}