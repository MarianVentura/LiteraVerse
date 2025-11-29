package edu.ucne.literaverse.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Chapters")
data class ChapterEntity(
    @PrimaryKey
    val chapterId: Int,
    val storyId: Int,
    val title: String,
    val content: String,
    val chapterNumber: Int,
    val isDraft: Boolean,
    val isPublished: Boolean,
    val createdAt: String,
    val publishedAt: String?,
    val updatedAt: String,
    val needsSync: Boolean = false
)