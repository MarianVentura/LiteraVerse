package edu.ucne.literaverse.presentation.createstory

sealed interface CreateStoryEvent {
    data class OnTitleChanged(val title: String) : CreateStoryEvent
    data class OnSynopsisChanged(val synopsis: String) : CreateStoryEvent
    data class OnGenreChanged(val genre: String) : CreateStoryEvent
    data class OnTagsChanged(val tags: String) : CreateStoryEvent
    object OnSaveAsDraft : CreateStoryEvent
    object OnPublish : CreateStoryEvent
}