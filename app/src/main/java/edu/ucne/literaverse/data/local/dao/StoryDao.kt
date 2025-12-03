package edu.ucne.literaverse.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import edu.ucne.literaverse.data.local.entities.StoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StoryDao {
    @Upsert
    suspend fun upsert(story: StoryEntity)

    @Query("SELECT * FROM Stories WHERE userId = :userId ORDER BY updatedAt DESC")
    fun getStoriesByUser(userId: Int): Flow<List<StoryEntity>>

    @Query("SELECT * FROM Stories WHERE storyId = :storyId")
    suspend fun getStoryById(storyId: Int): StoryEntity?

    @Query("DELETE FROM Stories WHERE storyId = :storyId")
    suspend fun delete(storyId: Int)

    @Query("SELECT * FROM Stories WHERE needsSync = 1")
    suspend fun getStoriesNeedingSync(): List<StoryEntity>

    @Query("UPDATE Stories SET needsSync = 0 WHERE storyId = :storyId")
    suspend fun markAsSynced(storyId: Int)

    @Query("SELECT * FROM Stories WHERE isFavorite = 1 ORDER BY lastReadAt DESC")
    fun getFavorites(): Flow<List<StoryEntity>>

    @Query("SELECT * FROM Stories WHERE isReading = 1 ORDER BY lastReadAt DESC")
    fun getReading(): Flow<List<StoryEntity>>

    @Query("SELECT * FROM Stories WHERE isCompleted = 1 ORDER BY lastReadAt DESC")
    fun getCompleted(): Flow<List<StoryEntity>>

    @Query("UPDATE Stories SET isFavorite = :isFavorite WHERE storyId = :storyId")
    suspend fun updateFavoriteStatus(storyId: Int, isFavorite: Boolean)

    @Query("UPDATE Stories SET isReading = :isReading WHERE storyId = :storyId")
    suspend fun updateReadingStatus(storyId: Int, isReading: Boolean)

    @Query("UPDATE Stories SET isCompleted = :isCompleted WHERE storyId = :storyId")
    suspend fun updateCompletedStatus(storyId: Int, isCompleted: Boolean)

    @Transaction
    @Query("""
        UPDATE Stories 
        SET lastReadChapterId = :chapterId,
            scrollPosition = :scrollPosition,
            lastReadAt = :lastReadAt,
            isReading = 1
        WHERE storyId = :storyId
    """)
    suspend fun updateReadingProgress(
        storyId: Int,
        chapterId: Int,
        scrollPosition: Double,
        lastReadAt: Long
    )

    @Query("SELECT * FROM Stories WHERE storyId = :storyId")
    fun getStoryFlow(storyId: Int): Flow<StoryEntity?>
}