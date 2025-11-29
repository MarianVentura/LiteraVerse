package edu.ucne.literaverse.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Stories")
data class StoryEntity(
    @PrimaryKey
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
    val needsSync: Boolean = false
)