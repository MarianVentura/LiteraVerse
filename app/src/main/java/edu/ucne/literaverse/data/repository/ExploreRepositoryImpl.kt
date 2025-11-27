package edu.ucne.literaverse.data.repository

import edu.ucne.literaverse.data.mappers.toDomain
import edu.ucne.literaverse.data.remote.RemoteDataSource
import edu.ucne.literaverse.data.remote.Resource
import edu.ucne.literaverse.domain.model.Genre
import edu.ucne.literaverse.domain.model.Story
import edu.ucne.literaverse.domain.repository.ExploreRepository
import javax.inject.Inject

class ExploreRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource
) : ExploreRepository {

    override suspend fun getFeaturedStories(): Resource<List<Story>> {
        return when (val result = remoteDataSource.getFeaturedStories()) {
            is Resource.Success -> {
                val stories = result.data?.map { it.toDomain() } ?: emptyList()
                Resource.Success(stories)
            }
            is Resource.Error -> Resource.Error(result.message ?: "Error al obtener destacadas")
            is Resource.Loading -> Resource.Loading()
        }
    }

    override suspend fun getPopularStories(): Resource<List<Story>> {
        return when (val result = remoteDataSource.getPopularStories()) {
            is Resource.Success -> {
                val stories = result.data?.map { it.toDomain() } ?: emptyList()
                Resource.Success(stories)
            }
            is Resource.Error -> Resource.Error(result.message ?: "Error al obtener más leídas")
            is Resource.Loading -> Resource.Loading()
        }
    }

    override suspend fun getNewStories(): Resource<List<Story>> {
        return when (val result = remoteDataSource.getNewStories()) {
            is Resource.Success -> {
                val stories = result.data?.map { it.toDomain() } ?: emptyList()
                Resource.Success(stories)
            }
            is Resource.Error -> Resource.Error(result.message ?: "Error al obtener nuevas")
            is Resource.Loading -> Resource.Loading()
        }
    }

    override suspend fun getGenres(): Resource<List<Genre>> {
        return when (val result = remoteDataSource.getGenres()) {
            is Resource.Success -> {
                val genres = result.data?.map { it.toDomain() } ?: emptyList()
                Resource.Success(genres)
            }
            is Resource.Error -> Resource.Error(result.message ?: "Error al obtener géneros")
            is Resource.Loading -> Resource.Loading()
        }
    }

    override suspend fun getStoriesByGenre(genreName: String): Resource<List<Story>> {
        return when (val result = remoteDataSource.getStoriesByGenre(genreName)) {
            is Resource.Success -> {
                val stories = result.data?.map { it.toDomain() } ?: emptyList()
                Resource.Success(stories)
            }
            is Resource.Error -> Resource.Error(result.message ?: "Error al obtener historias por género")
            is Resource.Loading -> Resource.Loading()
        }
    }
}