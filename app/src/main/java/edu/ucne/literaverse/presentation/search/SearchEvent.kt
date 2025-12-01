package edu.ucne.literaverse.presentation.search

import edu.ucne.literaverse.domain.model.EstadoNovel
import edu.ucne.literaverse.domain.model.OrdenCriterio

sealed interface SearchEvent {
    data class OnQueryChange(val query: String) : SearchEvent
    data class OnGenreSelect(val genero: String?) : SearchEvent
    data class OnCategorySelect(val categoria: String?) : SearchEvent
    data class OnEstadoSelect(val estado: EstadoNovel?) : SearchEvent
    data class OnOrdenChange(val orden: OrdenCriterio) : SearchEvent
    data class OnNovelClick(val novelId: Int) : SearchEvent
    object OnSearch : SearchEvent
    object ClearFilters : SearchEvent
    object ToggleFilters : SearchEvent
    object UserMessageShown : SearchEvent
}