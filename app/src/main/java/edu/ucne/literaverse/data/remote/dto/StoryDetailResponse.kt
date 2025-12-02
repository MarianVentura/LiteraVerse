package edu.ucne.literaverse.data.remote.dto
import java.time.LocalDateTime



data class StoryDetailResponse(
    val storyId: Int,
    val userId: Int,
    val userName: String?,
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
    val viewCount: Int
)