package edu.ucne.literaverse.data.remote.dto

data class CreateStoryRequest(
    val userId: Int,
    val title: String,
    val synopsis: String,
    val genre: String,
    val tags: String?
)