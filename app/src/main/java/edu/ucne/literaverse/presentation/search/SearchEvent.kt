package edu.ucne.literaverse.presentation.search


import edu.ucne.literaverse.domain.model.SortCriteria


sealed interface SearchEvent {
    data class OnQueryChange(val query: String) : SearchEvent
    data class OnGenreSelect(val genre: String?) : SearchEvent
    data class OnStatusSelect(val status: String?) : SearchEvent
    data class OnSortChange(val sortBy: SortCriteria) : SearchEvent
    data class OnStoryClick(val storyId: Int) : SearchEvent
    object OnSearch : SearchEvent
    object ClearFilters : SearchEvent
    object ToggleFilters : SearchEvent
    object UserMessageShown : SearchEvent
}


