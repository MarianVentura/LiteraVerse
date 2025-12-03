package edu.ucne.literaverse.domain.model

data class ReadingProgress(
    val progressId: Int,
    val userId: Int,
    val storyId: Int,
    val chapterId: Int,
    val scrollPosition: Double,
    val lastReadAt: String,
    val storyTitle: String?,
    val chapterTitle: String?
)