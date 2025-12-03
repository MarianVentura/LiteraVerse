package edu.ucne.literaverse.data.repository

import edu.ucne.literaverse.data.local.dao.StoryDao
import edu.ucne.literaverse.data.mappers.toCreateRequest
import edu.ucne.literaverse.data.mappers.toDomain
import edu.ucne.literaverse.data.mappers.toEntity
import edu.ucne.literaverse.data.mappers.toStoryReader
import edu.ucne.literaverse.data.mappers.toUpdateRequest
import edu.ucne.literaverse.data.remote.RemoteDataSource
import edu.ucne.literaverse.data.remote.Resource
import edu.ucne.literaverse.data.remote.dto.CreateStoryRequest
import edu.ucne.literaverse.data.remote.dto.UpdateStoryRequest
import edu.ucne.literaverse.data.remote.dto.StoryDetailResponse
import edu.ucne.literaverse.domain.model.StoryDetail
import edu.ucne.literaverse.domain.model.StoryReader
import edu.ucne.literaverse.domain.repository.StoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class StoryRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val storyDao: StoryDao
) : StoryRepository {

    override suspend fun createStory(
        userId: Int,
        title: String,
        synopsis: String,
        genre: String,
        tags: String?
    ): Resource<StoryDetail> {
        val request = CreateStoryRequest(
            userId = userId,
            title = title,
            synopsis = synopsis,
            genre = genre,
            tags = tags
        )

        return when (val result = remoteDataSource.createStory(request)) {
            is Resource.Success -> {
                result.data?.let { response ->
                    storyDao.upsert(response.toEntity())
                    val storyDetail = StoryDetail(
                        storyId = response.storyId,
                        userId = response.userId,
                        userName = response.userName,
                        title = response.title,
                        synopsis = response.synopsis,
                        coverImageUrl = response.coverImageUrl,
                        genre = response.genre,
                        tags = response.tags,
                        isDraft = response.isDraft,
                        isPublished = response.isPublished,
                        createdAt = response.createdAt,
                        publishedAt = response.publishedAt,
                        updatedAt = response.updatedAt,
                        viewCount = response.viewCount,
                        chapters = emptyList()
                    )
                    Resource.Success(storyDetail)
                } ?: Resource.Error("Error al crear historia")
            }
            is Resource.Error -> Resource.Error(result.message ?: "Error al crear historia")
            is Resource.Loading -> Resource.Loading()
        }
    }

    override suspend fun getStoriesByUser(userId: Int): Flow<List<StoryDetail>> {
        when (val result = remoteDataSource.getStoriesByUser(userId)) {
            is Resource.Success -> {
                result.data?.forEach { storyDao.upsert(it.toEntity()) }
            }
            else -> {}
        }

        return storyDao.getStoriesByUser(userId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getStoryById(storyId: Int): Resource<StoryDetail> {
        return when (val result = remoteDataSource.getStoryById(storyId)) {
            is Resource.Success -> {
                result.data?.let { response ->
                    val storyDetail = response.toDomain()
                    storyDao.upsert(storyDetail.toEntity())
                    Resource.Success(storyDetail)
                } ?: Resource.Error("Historia no encontrada")
            }
            is Resource.Error -> {
                val localStory = storyDao.getStoryById(storyId)
                if (localStory != null) {
                    Resource.Success(localStory.toDomain())
                } else {
                    Resource.Error(result.message ?: "Error al obtener historia")
                }
            }
            is Resource.Loading -> Resource.Loading()
        }
    }

    override suspend fun updateStory(
        storyId: Int,
        title: String,
        synopsis: String,
        genre: String,
        tags: String?
    ): Resource<StoryDetail> {
        val request = UpdateStoryRequest(
            title = title,
            synopsis = synopsis,
            genre = genre,
            tags = tags
        )

        return when (val result = remoteDataSource.updateStory(storyId, request)) {
            is Resource.Success -> {
                result.data?.let { response ->
                    storyDao.upsert(response.toEntity())
                    val storyDetail = StoryDetail(
                        storyId = response.storyId,
                        userId = response.userId,
                        userName = response.userName,
                        title = response.title,
                        synopsis = response.synopsis,
                        coverImageUrl = response.coverImageUrl,
                        genre = response.genre,
                        tags = response.tags,
                        isDraft = response.isDraft,
                        isPublished = response.isPublished,
                        createdAt = response.createdAt,
                        publishedAt = response.publishedAt,
                        updatedAt = response.updatedAt,
                        viewCount = response.viewCount,
                        chapters = emptyList()
                    )
                    Resource.Success(storyDetail)
                } ?: Resource.Error("Error al actualizar historia")
            }
            is Resource.Error -> Resource.Error(result.message ?: "Error al actualizar historia")
            is Resource.Loading -> Resource.Loading()
        }
    }

    override suspend fun deleteStory(storyId: Int): Resource<Unit> {
        return when (val result = remoteDataSource.deleteStory(storyId)) {
            is Resource.Success -> {
                storyDao.delete(storyId)
                Resource.Success(Unit)
            }
            is Resource.Error -> Resource.Error(result.message ?: "Error al eliminar historia")
            is Resource.Loading -> Resource.Loading()
        }
    }

    override suspend fun publishStory(storyId: Int): Resource<Unit> {
        return when (val result = remoteDataSource.publishStory(storyId)) {
            is Resource.Success -> {
                val story = storyDao.getStoryById(storyId)
                story?.let {
                    storyDao.upsert(it.copy(isPublished = true, isDraft = false))
                }
                Resource.Success(Unit)
            }
            is Resource.Error -> Resource.Error(result.message ?: "Error al publicar historia")
            is Resource.Loading -> Resource.Loading()
        }
    }

    override suspend fun unpublishStory(storyId: Int): Resource<Unit> {
        return when (val result = remoteDataSource.unpublishStory(storyId)) {
            is Resource.Success -> {
                val story = storyDao.getStoryById(storyId)
                story?.let {
                    storyDao.upsert(it.copy(isPublished = false, isDraft = true))
                }
                Resource.Success(Unit)
            }
            is Resource.Error -> Resource.Error(result.message ?: "Error al despublicar historia")
            is Resource.Loading -> Resource.Loading()
        }
    }

    override suspend fun syncStories() {
        val storiesToSync = storyDao.getStoriesNeedingSync()
        storiesToSync.forEach { story ->
            storyDao.markAsSynced(story.storyId)
        }
    }
    override suspend fun getStoryForReader(storyId: Int): Resource<StoryReader> {
        return when (val result = remoteDataSource.getStoryForReader(storyId)) {
            is Resource.Success -> {
                result.data?.let { (storyResponse, chaptersResponse) ->
                    val storyReader = storyResponse.toStoryReader(chaptersResponse)
                    Resource.Success(storyReader)
                } ?: Resource.Error("Error al procesar datos")
            }
            is Resource.Error -> Resource.Error(result.message ?: "Error al obtener historia")
            is Resource.Loading -> Resource.Loading()
        }
    }
}


