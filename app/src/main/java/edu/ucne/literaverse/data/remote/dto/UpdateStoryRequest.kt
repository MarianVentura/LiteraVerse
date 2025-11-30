package edu.ucne.literaverse.data.remote.dto

data class UpdateStoryRequest(
    val title: String,
    val synopsis: String,
    val genre: String,
    val tags: String?
)