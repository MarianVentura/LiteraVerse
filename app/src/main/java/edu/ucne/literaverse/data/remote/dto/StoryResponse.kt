package edu.ucne.literaverse.data.remote.dto


data class StoryResponse(
    val storyId: Int,
    val userId: Int,
    val userName: String?,
    val title: String,
    val synopsis: String,
    val coverImageUrl: String?,
    val isDraft: Boolean,
    val isPublished: Boolean,
    val createdAt: String,
    val publishedAt: String?,
    val updatedAt: String,
    val viewCount: Int,
    val genre: String?,
    val tags: String?
)


