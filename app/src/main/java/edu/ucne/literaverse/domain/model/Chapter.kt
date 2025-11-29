package edu.ucne.literaverse.domain.model

data class Chapter(
    val chapterId: Int,
    val storyId: Int,
    val title: String,
    val content: String,
    val chapterNumber: Int,
    val isDraft: Boolean,
    val isPublished: Boolean,
    val createdAt: String,
    val publishedAt: String?,
    val updatedAt: String
)