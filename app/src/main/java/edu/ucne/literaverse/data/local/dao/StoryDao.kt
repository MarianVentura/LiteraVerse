package edu.ucne.literaverse.data.local.dao

import androidx.room.Dao
import androidx.room.Query
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
}