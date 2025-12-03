package edu.ucne.literaverse.data.repository

import edu.ucne.literaverse.data.local.dao.ChapterDao
import edu.ucne.literaverse.data.local.dao.StoryDao
import edu.ucne.literaverse.data.mappers.toDomain
import edu.ucne.literaverse.data.mappers.toEntity
import edu.ucne.literaverse.data.mappers.toStoryWithProgressList
import edu.ucne.literaverse.data.remote.RemoteDataSource
import edu.ucne.literaverse.data.remote.Resource
import edu.ucne.literaverse.data.remote.dto.ReadingProgressRequest
import edu.ucne.literaverse.domain.model.StoryWithProgress
import edu.ucne.literaverse.domain.repository.LibraryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LibraryRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val storyDao: StoryDao,
    private val chapterDao: ChapterDao
) : LibraryRepository {

    override fun getFavorites(userId: Int): Flow<List<StoryWithProgress>> {
        return combine(
            storyDao.getFavorites(),
            storyDao.getStoriesByUser(userId)
        ) { favorites, _ ->
            val progressList = when (val result = remoteDataSource.getReadingProgress(userId)) {
                is Resource.Success -> result.data?.map { it.toDomain() } ?: emptyList()
                else -> emptyList()
            }

            val chaptersCount = favorites.associate { story ->
                story.storyId to (chapterDao.getChaptersByStory(story.storyId).hashCode())
            }

            favorites.toStoryWithProgressList(progressList, chaptersCount)
        }
    }

    override suspend fun addFavorite(userId: Int, storyId: Int): Resource<Unit> {
        storyDao.updateFavoriteStatus(storyId, true)
        return when (val result = remoteDataSource.addFavorite(userId, storyId)) {
            is Resource.Success -> Resource.Success(Unit)
            is Resource.Error -> {
                storyDao.updateFavoriteStatus(storyId, false)
                Resource.Error(result.message ?: "Error al agregar favorito")
            }
            is Resource.Loading -> Resource.Loading()
        }
    }

    override suspend fun removeFavorite(userId: Int, storyId: Int): Resource<Unit> {
        storyDao.updateFavoriteStatus(storyId, false)
        return when (val result = remoteDataSource.removeFavorite(userId, storyId)) {
            is Resource.Success -> Resource.Success(Unit)
            is Resource.Error -> {
                storyDao.updateFavoriteStatus(storyId, true)
                Resource.Error(result.message ?: "Error al remover favorito")
            }
            is Resource.Loading -> Resource.Loading()
        }
    }

    override suspend fun isFavorite(userId: Int, storyId: Int): Resource<Boolean> {
        return remoteDataSource.isFavorite(userId, storyId)
    }

    override fun getReadingStories(userId: Int): Flow<List<StoryWithProgress>> {
        return combine(
            storyDao.getReading(),
            storyDao.getStoriesByUser(userId)
        ) { reading, _ ->
            val progressList = when (val result = remoteDataSource.getReadingProgress(userId)) {
                is Resource.Success -> result.data?.map { it.toDomain() } ?: emptyList()
                else -> emptyList()
            }

            val chaptersCount = reading.associate { story ->
                story.storyId to 0
            }

            reading.toStoryWithProgressList(progressList, chaptersCount)
        }
    }

    override suspend fun saveReadingProgress(
        userId: Int,
        storyId: Int,
        chapterId: Int,
        scrollPosition: Double
    ): Resource<Unit> {
        storyDao.updateReadingProgress(
            storyId = storyId,
            chapterId = chapterId,
            scrollPosition = scrollPosition,
            lastReadAt = System.currentTimeMillis()
        )

        val request = ReadingProgressRequest(
            userId = userId,
            storyId = storyId,
            chapterId = chapterId,
            scrollPosition = scrollPosition
        )

        return when (val result = remoteDataSource.saveReadingProgress(request)) {
            is Resource.Success -> Resource.Success(Unit)
            is Resource.Error -> Resource.Error(result.message ?: "Error al guardar progreso")
            is Resource.Loading -> Resource.Loading()
        }
    }

    override fun getCompletedStories(userId: Int): Flow<List<StoryWithProgress>> {
        return combine(
            storyDao.getCompleted(),
            storyDao.getStoriesByUser(userId)
        ) { completed, _ ->
            val progressList = when (val result = remoteDataSource.getReadingProgress(userId)) {
                is Resource.Success -> result.data?.map { it.toDomain() } ?: emptyList()
                else -> emptyList()
            }

            val chaptersCount = completed.associate { story ->
                story.storyId to 0
            }

            completed.toStoryWithProgressList(progressList, chaptersCount)
        }
    }

    override suspend fun markAsCompleted(userId: Int, storyId: Int): Resource<Unit> {
        storyDao.updateCompletedStatus(storyId, true)
        storyDao.updateReadingStatus(storyId, false)
        return Resource.Success(Unit)
    }

    override suspend fun updateReadingStatus(userId: Int, storyId: Int, isReading: Boolean): Resource<Unit> {
        storyDao.updateReadingStatus(storyId, isReading)
        return Resource.Success(Unit)
    }

    override suspend fun syncLibrary(userId: Int) {
        when (val favResult = remoteDataSource.getFavorites(userId)) {
            is Resource.Success -> {
                favResult.data?.forEach { storyResponse ->
                    val entity = storyResponse.toEntity().copy(isFavorite = true)
                    storyDao.upsert(entity)
                }
            }
            else -> {}
        }

        when (val progResult = remoteDataSource.getReadingProgress(userId)) {
            is Resource.Success -> {
                progResult.data?.forEach { progress ->
                    val story = storyDao.getStoryById(progress.storyId)
                    story?.let {
                        storyDao.updateReadingProgress(
                            storyId = progress.storyId,
                            chapterId = progress.chapterId,
                            scrollPosition = progress.scrollPosition,
                            lastReadAt = System.currentTimeMillis()
                        )
                    }
                }
            }
            else -> {}
        }

        when (val compResult = remoteDataSource.getCompletedStories(userId)) {
            is Resource.Success -> {
                compResult.data?.forEach { storyResponse ->
                    val entity = storyResponse.toEntity().copy(isCompleted = true)
                    storyDao.upsert(entity)
                }
            }
            else -> {}
        }
    }
}