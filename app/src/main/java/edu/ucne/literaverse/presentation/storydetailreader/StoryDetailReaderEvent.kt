package edu.ucne.literaverse.presentation.storydetailreader

import edu.ucne.literaverse.presentation.components.LibraryStates

sealed interface StoryDetailReaderEvent {
    data class LoadStory(val storyId: Int) : StoryDetailReaderEvent
    data class OnChapterClick(val chapterId: Int) : StoryDetailReaderEvent
    data class OnStartReading(val onNavigate: (Int, Int) -> Unit) : StoryDetailReaderEvent
    object OnToggleFavorite : StoryDetailReaderEvent

    object OnAddToLibrary : StoryDetailReaderEvent
    data object ShowLibraryMenu : StoryDetailReaderEvent
    data object DismissLibraryMenu : StoryDetailReaderEvent
    data class UpdateLibraryStates(val states: LibraryStates) : StoryDetailReaderEvent

    object UserMessageShown : StoryDetailReaderEvent
}