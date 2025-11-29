package edu.ucne.literaverse.domain.repository

import edu.ucne.literaverse.data.remote.Resource
import edu.ucne.literaverse.domain.model.Chapter
import kotlinx.coroutines.flow.Flow

interface ChapterRepository {
    suspend fun createChapter(
        storyId: Int,
        title: String,
        content: String,
        chapterNumber: Int,
        isDraft: Boolean
    ): Resource<Chapter>

    fun getChaptersByStory(storyId: Int): Flow<List<Chapter>>

    suspend fun getChapterById(storyId: Int, chapterId: Int): Resource<Chapter>

    suspend fun updateChapter(
        storyId: Int,
        chapterId: Int,
        title: String,
        content: String,
        isDraft: Boolean
    ): Resource<Chapter>

    suspend fun deleteChapter(storyId: Int, chapterId: Int): Resource<Unit>

    suspend fun publishChapter(storyId: Int, chapterId: Int): Resource<Unit>

    suspend fun unpublishChapter(storyId: Int, chapterId: Int): Resource<Unit>

    suspend fun syncChapters()
}