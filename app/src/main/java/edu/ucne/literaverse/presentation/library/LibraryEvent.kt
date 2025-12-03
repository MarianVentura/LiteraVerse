package edu.ucne.literaverse.presentation.library

import edu.ucne.literaverse.presentation.components.LibraryStates

sealed interface LibraryEvent {
    data object LoadLibrary : LibraryEvent
    data class SelectTab(val tab: LibraryTab) : LibraryEvent
    data class RemoveFromFavorites(val storyId: Int) : LibraryEvent
    data class AddToFavorites(val storyId: Int) : LibraryEvent
    data class MarkAsCompleted(val storyId: Int) : LibraryEvent
    data class OnStoryClick(val storyId: Int, val lastReadChapterId: Int?) : LibraryEvent

    data class UpdateLibraryStates(val storyId: Int, val states: LibraryStates) : LibraryEvent
    data class ShowLibraryMenu(val storyId: Int, val currentStates: LibraryStates) : LibraryEvent
    data object DismissLibraryMenu : LibraryEvent
    data class ShowContextMenu(val storyId: Int, val currentStates: LibraryStates) : LibraryEvent
    data object DismissContextMenu : LibraryEvent
    data class RemoveFromLibrary(val storyId: Int) : LibraryEvent
    data object Refresh : LibraryEvent
    data object UserMessageShown : LibraryEvent
}