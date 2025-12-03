package edu.ucne.literaverse.presentation.library

sealed interface LibraryEvent {
    data object LoadLibrary : LibraryEvent
    data class SelectTab(val tab: LibraryTab) : LibraryEvent
    data class RemoveFromFavorites(val storyId: Int) : LibraryEvent
    data class AddToFavorites(val storyId: Int) : LibraryEvent
    data class MarkAsCompleted(val storyId: Int) : LibraryEvent
    data class OnStoryClick(val storyId: Int, val lastReadChapterId: Int?) : LibraryEvent
    data object Refresh : LibraryEvent
    data object UserMessageShown : LibraryEvent
}