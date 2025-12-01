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


class SearchRepositoryImpl @Inject constructor(
    private val api: LiteraVerseApi
) : SearchRepository {


    private val _searchResults = MutableStateFlow<List<Story>>(emptyList())


    override fun observeSearchResults(): Flow<List<Story>> =
        _searchResults.asStateFlow()


    override suspend fun searchStories(filters: SearchFilters): Resource<List<Story>> {
        return try {
            android.util.Log.d("SearchRepo", "Buscando con query='${filters.query}', genre='${filters.genre}', status='${filters.status}'")


            val response = api.searchStories(
                query = filters.query.ifBlank { null },
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
                Resource.Error("Error al buscar: ${response.message()}")
            }
        } catch (e: Exception) {
            android.util.Log.e("SearchRepo", "Exception: ${e.message}", e)
            Resource.Error(e.localizedMessage ?: "Error de conexión")
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
