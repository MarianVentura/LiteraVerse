package edu.ucne.literaverse.data.remote.dto

data class ReadingProgressRequest(
    val userId: Int,
    val storyId: Int,
    val chapterId: Int,
    val scrollPosition: Double
)