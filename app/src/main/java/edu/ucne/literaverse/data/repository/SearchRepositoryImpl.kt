package edu.ucne.literaverse.data.repository

import edu.ucne.literaverse.data.mappers.toDomain
import edu.ucne.literaverse.data.remote.LiteraVerseApi
import edu.ucne.literaverse.data.remote.Resource
import edu.ucne.literaverse.domain.model.SearchFilters
import edu.ucne.literaverse.domain.model.Story
import edu.ucne.literaverse.domain.repository.SearchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import retrofit2.HttpException
import java.io.IOException

class SearchRepositoryImpl @Inject constructor(
    private val api: LiteraVerseApi
) : SearchRepository {

    private val _searchResults = MutableStateFlow<List<Story>>(emptyList())

    override fun observeSearchResults(): Flow<List<Story>> =
        _searchResults.asStateFlow()

    override suspend fun searchStories(filters: SearchFilters): Resource<List<Story>> {
        return try {
            android.util.Log.d("SearchRepo", "Buscando con query='${filters.query}', genre='${filters.genre}', status='${filters.status}'")


            val queryParam = if (filters.query.isNullOrBlank()) "" else filters.query

            val response = api.searchStories(
                query = queryParam,
                genre = filters.genre,
                status = filters.status
            )

            android.util.Log.d("SearchRepo", "Response code: ${response.code()}")

            if (response.isSuccessful) {
                val stories = response.body()?.map { it.toDomain() } ?: emptyList()
                android.util.Log.d("SearchRepo", "Historias encontradas: ${stories.size}")
                stories.forEach {
                    android.util.Log.d("SearchRepo", "- ${it.title} by ${it.author}")
                }
                _searchResults.value = stories
                Resource.Success(stories)
            } else {
                android.util.Log.e("SearchRepo", "Error response: ${response.errorBody()?.string()}")

                Resource.Error(
                    message = "Error al buscar historias: Código ${response.code()} - ${response.message()}",
                    data = null
                )
            }
        } catch (e: HttpException) {

            android.util.Log.e("SearchRepo", "HTTP Exception: ${e.code()}", e)
            Resource.Error(
                message = "Error del servidor: ${e.code()} ${e.message()}",
                data = null
            )
        } catch (e: IOException) {

            android.util.Log.e("SearchRepo", "Network Exception: ${e.message}", e)
            Resource.Error(
                message = "Error de conexión: ${e.localizedMessage ?: "No se pudo conectar al servidor"}",
                data = null
            )
        } catch (e: Exception) {

            android.util.Log.e("SearchRepo", "General Exception: ${e.message}", e)
            Resource.Error(
                message = e.localizedMessage ?: "Error desconocido al buscar historias",
                data = null
            )
        }
    }

    override suspend fun getPopularStories(): Resource<List<Story>> {
        return try {
            val response = api.getPopularStories()

            if (response.isSuccessful) {
                val stories = response.body()?.map { it.toDomain() } ?: emptyList()
                Resource.Success(stories)
            } else {
                Resource.Error("Error al cargar historias populares")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error de conexión")
        }
    }

    override suspend fun getRecentStories(): Resource<List<Story>> {
        return try {
            val response = api.getNewStories()

            if (response.isSuccessful) {
                val stories = response.body()?.map { it.toDomain() } ?: emptyList()
                Resource.Success(stories)
            } else {
                Resource.Error("Error al cargar historias recientes")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error de conexión")
        }
    }
}