package edu.ucne.literaverse.data.remote.dto

data class StoryReaderDto(
    val storyId: Int,
    val title: String,
    val synopsis: String,
    val coverImageUrl: String?,
    val author: String,
    val authorId: Int,
    val genre: String?,
    val tags: String?,
    val viewCount: Int,
    val likeCount: Int = 0,
    val chapters: List<ChapterResponse>
)