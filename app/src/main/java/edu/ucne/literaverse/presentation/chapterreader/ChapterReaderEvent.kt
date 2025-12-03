package edu.ucne.literaverse.presentation.chapterreader

sealed interface ChapterReaderEvent {
    data class LoadChapter(val storyId: Int, val chapterId: Int) : ChapterReaderEvent
    data class OnScrollPositionChanged(val position: Double) : ChapterReaderEvent
    object OnNextChapter : ChapterReaderEvent
    object OnPreviousChapter : ChapterReaderEvent
    object OnToggleMenu : ChapterReaderEvent
    object SaveProgress : ChapterReaderEvent
    object UserMessageShown : ChapterReaderEvent
}