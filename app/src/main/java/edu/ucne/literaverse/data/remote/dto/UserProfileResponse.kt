package edu.ucne.literaverse.data.remote.dto

data class UserProfileResponse(
    val userId: Int,
    val userName: String,
    val storiesCount: Int,
    val publishedStoriesCount: Int,
    val totalViews: Int,
    val favoritesCount: Int
)