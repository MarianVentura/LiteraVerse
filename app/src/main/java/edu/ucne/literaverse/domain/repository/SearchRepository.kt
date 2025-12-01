package edu.ucne.literaverse.domain.repository

import edu.ucne.literaverse.data.remote.Resource
import edu.ucne.literaverse.domain.model.Novel
import edu.ucne.literaverse.domain.model.SearchFilters
import kotlinx.coroutines.flow.Flow

interface SearchRepository {
    suspend fun searchNovels(filters: SearchFilters): Resource<List<Novel>>
    suspend fun getPopularNovels(): Resource<List<Novel>>
    suspend fun getRecentNovels(): Resource<List<Novel>>
    fun observeSearchResults(): Flow<List<Novel>>
}