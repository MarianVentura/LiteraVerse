package edu.ucne.literaverse.data.remote.dto

data class StoryResponse(
    val storyId: Int,
    val title: String,
    val author: String,
    val description: String,
    val coverImageUrl: String?,
    val genres: List<String>,
    val reads: Int,
    val chapters: Int,
    val status: String
)