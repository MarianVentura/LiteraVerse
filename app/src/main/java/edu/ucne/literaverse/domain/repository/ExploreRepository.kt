package edu.ucne.literaverse.domain.repository

import edu.ucne.literaverse.data.remote.Resource
import edu.ucne.literaverse.domain.model.Genre
import edu.ucne.literaverse.domain.model.Story

interface ExploreRepository {
    suspend fun getFeaturedStories(): Resource<List<Story>>
    suspend fun getPopularStories(): Resource<List<Story>>
    suspend fun getNewStories(): Resource<List<Story>>
    suspend fun getGenres(): Resource<List<Genre>>
    suspend fun getStoriesByGenre(genreName: String): Resource<List<Story>>
}