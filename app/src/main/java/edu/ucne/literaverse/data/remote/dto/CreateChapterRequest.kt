package edu.ucne.literaverse.data.remote.dto

data class CreateChapterRequest(
    val title: String,
    val content: String,
    val chapterNumber: Int,
    val isDraft: Boolean
)