package edu.ucne.literaverse.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import edu.ucne.literaverse.data.local.entities.ChapterEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChapterDao {
    @Upsert
    suspend fun upsert(chapter: ChapterEntity)

    @Query("SELECT * FROM Chapters WHERE storyId = :storyId ORDER BY chapterNumber ASC")
    fun getChaptersByStory(storyId: Int): Flow<List<ChapterEntity>>

    @Query("SELECT * FROM Chapters WHERE chapterId = :chapterId")
    suspend fun getChapterById(chapterId: Int): ChapterEntity?

    @Query("DELETE FROM Chapters WHERE chapterId = :chapterId")
    suspend fun delete(chapterId: Int)

    @Query("SELECT * FROM Chapters WHERE needsSync = 1")
    suspend fun getChaptersNeedingSync(): List<ChapterEntity>

    @Query("UPDATE Chapters SET needsSync = 0 WHERE chapterId = :chapterId")
    suspend fun markAsSynced(chapterId: Int)

    @Query("SELECT * FROM Chapters WHERE storyId = :storyId ORDER BY chapterNumber ASC")
    suspend fun getChaptersByStorySync(storyId: Int): List<ChapterEntity>
}