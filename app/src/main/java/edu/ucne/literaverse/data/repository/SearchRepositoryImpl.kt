package edu.ucne.literaverse.data.repository

import edu.ucne.literaverse.data.mappers.toDomain
import edu.ucne.literaverse.data.remote.LiteraVerseApi
import edu.ucne.literaverse.data.remote.Resource
import edu.ucne.literaverse.domain.model.Novel
import edu.ucne.literaverse.domain.model.SearchFilters
import edu.ucne.literaverse.domain.repository.SearchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(
    private val api: LiteraVerseApi
) : SearchRepository {

    private val _searchResults = MutableStateFlow<List<Novel>>(emptyList())

    override fun observeSearchResults(): Flow<List<Novel>> =
        _searchResults.asStateFlow()

    override suspend fun searchNovels(filters: SearchFilters): Resource<List<Novel>> {
        return try {
            val response = api.searchNovels(
                query = filters.query.ifBlank { null },
                genero = filters.genero,
                categoria = filters.categoria,
                estado = filters.estado?.value,
                ordenarPor = filters.ordenarPor.value,
                orden = "desc"
            )

            if (response.isSuccessful) {
                val novels = response.body()?.data?.toDomain() ?: emptyList()
                _searchResults.value = novels
                Resource.Success(novels)
            } else {
                Resource.Error("Error al buscar: ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error de conexión")
        }
    }

    override suspend fun getPopularNovels(): Resource<List<Novel>> {
        return try {
            val response = api.getPopularNovels(limite = 20)

            if (response.isSuccessful) {
                val novels = response.body()?.data?.toDomain() ?: emptyList()
                Resource.Success(novels)
            } else {
                Resource.Error("Error al cargar novelas populares")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error de conexión")
        }
    }

    override suspend fun getRecentNovels(): Resource<List<Novel>> {
        return try {
            val response = api.getRecentNovels(limite = 20)

            if (response.isSuccessful) {
                val novels = response.body()?.data?.toDomain() ?: emptyList()
                Resource.Success(novels)
            } else {
                Resource.Error("Error al cargar novelas recientes")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error de conexión")
        }
    }
}