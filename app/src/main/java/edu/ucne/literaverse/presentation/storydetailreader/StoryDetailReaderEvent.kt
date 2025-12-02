package edu.ucne.literaverse.presentation.storydetailreader

sealed interface StoryDetailReaderEvent {
    data class LoadStory(val storyId: Int) : StoryDetailReaderEvent
    data class OnChapterClick(val chapterId: Int) : StoryDetailReaderEvent
    object OnStartReading : StoryDetailReaderEvent
    object OnToggleFavorite : StoryDetailReaderEvent

    object OnAddToLibrary : StoryDetailReaderEvent

    object UserMessageShown : StoryDetailReaderEvent
}