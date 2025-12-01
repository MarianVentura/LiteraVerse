package edu.ucne.literaverse.presentation.home

sealed interface HomeEvent {
    object LoadHomeData : HomeEvent
    data class OnGenreSelected(val genre: String) : HomeEvent
    object Refresh : HomeEvent

    data object Logout : HomeEvent
}