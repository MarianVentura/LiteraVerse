package edu.ucne.literaverse.presentation.chaptereditor

sealed interface ChapterEditorEvent {
    data class OnTitleChanged(val title: String) : ChapterEditorEvent
    data class OnContentChanged(val content: String) : ChapterEditorEvent
    object OnSaveAsDraft : ChapterEditorEvent
    object OnPublish : ChapterEditorEvent
}