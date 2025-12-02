package edu.ucne.literaverse.domain.model

data class StoryReader(
    val storyId: Int,
    val title: String,
    val synopsis: String,
    val coverImageUrl: String?,
    val author: String,
    val authorId: Int,
    val genre: String?,
    val tags: String?,
    val viewCount: Int,
    val publishedChapters: List<Chapter>
)