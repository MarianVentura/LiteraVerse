package edu.ucne.literaverse.data.repository

import edu.ucne.literaverse.data.local.dao.ChapterDao
import edu.ucne.literaverse.data.mappers.toDomain
import edu.ucne.literaverse.data.mappers.toEntity
import edu.ucne.literaverse.data.remote.RemoteDataSource
import edu.ucne.literaverse.data.remote.Resource
import edu.ucne.literaverse.data.remote.dto.CreateChapterRequest
import edu.ucne.literaverse.data.remote.dto.UpdateChapterRequest
import edu.ucne.literaverse.domain.model.Chapter
import edu.ucne.literaverse.domain.repository.ChapterRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ChapterRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val chapterDao: ChapterDao
) : ChapterRepository {

    override suspend fun createChapter(
        storyId: Int,
        title: String,
        content: String,
        chapterNumber: Int,
        isDraft: Boolean
    ): Resource<Chapter> {
        val request = CreateChapterRequest(
            title = title,
            content = content,
            chapterNumber = chapterNumber,
            isDraft = isDraft
        )

        return when (val result = remoteDataSource.createChapter(storyId, request)) {
            is Resource.Success -> {
                result.data?.let { response ->
                    val chapter = response.toDomain()
                    chapterDao.upsert(chapter.toEntity())
                    Resource.Success(chapter)
                } ?: Resource.Error("Error al crear capítulo")
            }
            is Resource.Error -> Resource.Error(result.message ?: "Error al crear capítulo")
            is Resource.Loading -> Resource.Loading()
        }
    }

    override fun getChaptersByStory(storyId: Int): Flow<List<Chapter>> {
        return chapterDao.getChaptersByStory(storyId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getChapterById(storyId: Int, chapterId: Int): Resource<Chapter> {
        return when (val result = remoteDataSource.getChapterById(storyId, chapterId)) {
            is Resource.Success -> {
                result.data?.let { response ->
                    val chapter = response.toDomain()
                    chapterDao.upsert(chapter.toEntity())
                    Resource.Success(chapter)
                } ?: Resource.Error("Capítulo no encontrado")
            }
            is Resource.Error -> {
                val localChapter = chapterDao.getChapterById(chapterId)
                if (localChapter != null) {
                    Resource.Success(localChapter.toDomain())
                } else {
                    Resource.Error(result.message ?: "Error al obtener capítulo")
                }
            }
            is Resource.Loading -> Resource.Loading()
        }
    }

    override suspend fun updateChapter(
        storyId: Int,
        chapterId: Int,
        title: String,
        content: String,
        isDraft: Boolean
    ): Resource<Chapter> {
        val request = UpdateChapterRequest(
            title = title,
            content = content,
            isDraft = isDraft
        )

        return when (val result = remoteDataSource.updateChapter(storyId, chapterId, request)) {
            is Resource.Success -> {
                result.data?.let { response ->
                    val chapter = response.toDomain()
                    chapterDao.upsert(chapter.toEntity())
                    Resource.Success(chapter)
                } ?: Resource.Error("Error al actualizar capítulo")
            }
            is Resource.Error -> Resource.Error(result.message ?: "Error al actualizar capítulo")
            is Resource.Loading -> Resource.Loading()
        }
    }

    override suspend fun deleteChapter(storyId: Int, chapterId: Int): Resource<Unit> {
        return when (val result = remoteDataSource.deleteChapter(storyId, chapterId)) {
            is Resource.Success -> {
                chapterDao.delete(chapterId)
                Resource.Success(Unit)
            }
            is Resource.Error -> Resource.Error(result.message ?: "Error al eliminar capítulo")
            is Resource.Loading -> Resource.Loading()
        }
    }

    override suspend fun publishChapter(storyId: Int, chapterId: Int): Resource<Unit> {
        return when (val result = remoteDataSource.publishChapter(storyId, chapterId)) {
            is Resource.Success -> {
                val chapter = chapterDao.getChapterById(chapterId)
                chapter?.let {
                    chapterDao.upsert(it.copy(isPublished = true, isDraft = false))
                }
                Resource.Success(Unit)
            }
            is Resource.Error -> Resource.Error(result.message ?: "Error al publicar capítulo")
            is Resource.Loading -> Resource.Loading()
        }
    }

    override suspend fun unpublishChapter(storyId: Int, chapterId: Int): Resource<Unit> {
        return when (val result = remoteDataSource.unpublishChapter(storyId, chapterId)) {
            is Resource.Success -> {
                val chapter = chapterDao.getChapterById(chapterId)
                chapter?.let {
                    chapterDao.upsert(it.copy(isPublished = false, isDraft = true))
                }
                Resource.Success(Unit)
            }
            is Resource.Error -> Resource.Error(result.message ?: "Error al despublicar capítulo")
            is Resource.Loading -> Resource.Loading()
        }
    }

    override suspend fun syncChapters() {
        val chaptersToSync = chapterDao.getChaptersNeedingSync()
        chaptersToSync.forEach { chapter ->
            chapterDao.markAsSynced(chapter.chapterId)
        }
    }

    override suspend fun getNextChapter(storyId: Int, currentChapterNumber: Int): Resource<Chapter?> {
        return try {
            val localChapters = chapterDao.getChaptersByStorySync(storyId)

            val nextChapter = localChapters
                .filter { it.isPublished && !it.isDraft }
                .sortedBy { it.chapterNumber }
                .firstOrNull { it.chapterNumber > currentChapterNumber }

            if (nextChapter != null) {
                Resource.Success(nextChapter.toDomain())
            } else {
                Resource.Success(null)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error al obtener siguiente capítulo")
        }
    }

    override suspend fun getPreviousChapter(storyId: Int, currentChapterNumber: Int): Resource<Chapter?> {
        return try {
            val localChapters = chapterDao.getChaptersByStorySync(storyId)

            val previousChapter = localChapters
                .filter { it.isPublished && !it.isDraft }
                .sortedBy { it.chapterNumber }
                .lastOrNull { it.chapterNumber < currentChapterNumber }

            if (previousChapter != null) {
                Resource.Success(previousChapter.toDomain())
            } else {
                Resource.Success(null)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error al obtener capítulo anterior")
        }
    }
}