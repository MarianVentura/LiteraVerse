package edu.ucne.literaverse.presentation.write

sealed interface WriterPanelEvent {
    object LoadWriterData : WriterPanelEvent
    object NavigateToMyStories : WriterPanelEvent
    object NavigateToCreateStory : WriterPanelEvent
    data class NavigateToStoryDetail(val storyId: Int) : WriterPanelEvent
}