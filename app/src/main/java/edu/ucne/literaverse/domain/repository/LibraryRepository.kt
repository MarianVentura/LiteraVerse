package edu.ucne.literaverse.domain.repository

import edu.ucne.literaverse.data.remote.Resource
import edu.ucne.literaverse.domain.model.StoryWithProgress
import kotlinx.coroutines.flow.Flow

interface LibraryRepository {
    fun getFavorites(userId: Int): Flow<List<StoryWithProgress>>
    suspend fun addFavorite(userId: Int, storyId: Int): Resource<Unit>
    suspend fun removeFavorite(userId: Int, storyId: Int): Resource<Unit>
    suspend fun isFavorite(userId: Int, storyId: Int): Resource<Boolean>

    fun getReadingStories(userId: Int): Flow<List<StoryWithProgress>>
    suspend fun saveReadingProgress(
        userId: Int,
        storyId: Int,
        chapterId: Int,
        scrollPosition: Double
    ): Resource<Unit>

    fun getCompletedStories(userId: Int): Flow<List<StoryWithProgress>>
    suspend fun markAsCompleted(userId: Int, storyId: Int): Resource<Unit>
    suspend fun updateReadingStatus(userId: Int, storyId: Int, isReading: Boolean): Resource<Unit>


    suspend fun syncLibrary(userId: Int)
}