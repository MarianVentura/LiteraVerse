package edu.ucne.literaverse.presentation.storychapters

sealed interface StoryChaptersEvent {
    data class LoadStory(val storyId: Int) : StoryChaptersEvent
    data class OnTabSelected(val index: Int) : StoryChaptersEvent
    data class OnChapterClick(val chapterId: Int) : StoryChaptersEvent
    object OnAddChapter : StoryChaptersEvent
    data class OnDeleteChapter(val chapterId: Int) : StoryChaptersEvent
    data class OnPublishChapter(val chapterId: Int) : StoryChaptersEvent
    object OnPublishStory : StoryChaptersEvent
    object OnEditStory : StoryChaptersEvent
}
