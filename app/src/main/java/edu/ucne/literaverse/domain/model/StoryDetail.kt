package edu.ucne.literaverse.domain.model

data class StoryDetail(
    val storyId: Int,
    val userId: Int,
    val title: String,
    val synopsis: String,
    val coverImageUrl: String?,
    val genre: String?,
    val tags: String?,
    val isDraft: Boolean,
    val isPublished: Boolean,
    val createdAt: String,
    val publishedAt: String?,
    val updatedAt: String,
    val viewCount: Int,
    val chapters: List<Chapter>
)