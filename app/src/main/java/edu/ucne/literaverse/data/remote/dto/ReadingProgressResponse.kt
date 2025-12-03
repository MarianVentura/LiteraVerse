package edu.ucne.literaverse.data.remote.dto

data class ReadingProgressResponse(
    val progressId: Int,
    val userId: Int,
    val storyId: Int,
    val chapterId: Int,
    val scrollPosition: Double,
    val lastReadAt: String,
    val storyTitle: String?,
    val chapterTitle: String?
)