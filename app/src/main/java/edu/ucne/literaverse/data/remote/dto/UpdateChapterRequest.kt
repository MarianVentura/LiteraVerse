package edu.ucne.literaverse.data.remote.dto

data class UpdateChapterRequest(
    val title: String,
    val content: String,
    val isDraft: Boolean
)