package edu.ucne.literaverse.domain.model

data class UserProfile(
    val userId: Int,
    val userName: String,
    val storiesCount: Int,
    val publishedStoriesCount: Int,
    val totalViews: Int,
    val favoritesCount: Int
)