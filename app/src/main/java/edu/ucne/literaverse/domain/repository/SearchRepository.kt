package edu.ucne.literaverse.domain.repository


import edu.ucne.literaverse.data.remote.Resource
import edu.ucne.literaverse.domain.model.SearchFilters
import edu.ucne.literaverse.domain.model.Story
import kotlinx.coroutines.flow.Flow


interface SearchRepository {
    suspend fun searchStories(filters: SearchFilters): Resource<List<Story>>
    suspend fun getPopularStories(): Resource<List<Story>>
    suspend fun getRecentStories(): Resource<List<Story>>
    fun observeSearchResults(): Flow<List<Story>>
}


