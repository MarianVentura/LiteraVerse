package edu.ucne.literaverse.presentation.mystories

sealed interface MyStoriesEvent {
    object LoadStories : MyStoriesEvent
    data class OnFilterChanged(val filter: StoryFilter) : MyStoriesEvent
    data class OnStoryClick(val storyId: Int) : MyStoriesEvent
    data class OnDeleteStory(val storyId: Int) : MyStoriesEvent
}