package edu.ucne.literaverse.presentation.chapterreader

import edu.ucne.literaverse.domain.model.Chapter
import edu.ucne.literaverse.domain.model.StoryDetail

data class ChapterReaderUiState(
    val isLoading: Boolean = false,
    val chapter: Chapter? = null,
    val story: StoryDetail? = null,
    val currentChapterNumber: Int = 1,
    val totalChapters: Int = 0,
    val scrollPosition: Double = 0.0,
    val hasNextChapter: Boolean = false,
    val hasPreviousChapter: Boolean = false,
    val showMenu: Boolean = false,
    val error: String? = null,
    val userMessage: String? = null
)