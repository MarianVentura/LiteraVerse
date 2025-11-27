package edu.ucne.literaverse.data.remote

import edu.ucne.literaverse.data.remote.dto.LoginRequest
import edu.ucne.literaverse.data.remote.dto.LoginResponse
import edu.ucne.literaverse.data.remote.dto.GenreResponse
import edu.ucne.literaverse.data.remote.dto.StoryResponse
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val literaVerseApi: LiteraVerseApi
) {
    suspend fun login(request: LoginRequest): Resource<LoginResponse> {
        return try {
            val response = literaVerseApi.createUsuario(request)
            if (response.isSuccessful) {
                response.body()?.let { Resource.Success(it) }
                    ?: Resource.Error("Respuesta vacía del servidor")
            } else {
                Resource.Error("HTTP ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error de red")
        }
    }

    suspend fun register(request: LoginRequest): Resource<LoginResponse> {
        return try {
            val response = literaVerseApi.createUsuario(request)
            if (response.isSuccessful) {
                response.body()?.let { Resource.Success(it) }
                    ?: Resource.Error("Respuesta vacía del servidor")
            } else {
                Resource.Error("HTTP ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error de red")
        }
    }

    suspend fun getUsuarios(): Resource<List<LoginResponse>> {
        return try {
            val response = literaVerseApi.getUsuarios()
            if (response.isSuccessful) {
                response.body()?.let { Resource.Success(it) }
                    ?: Resource.Error("Respuesta vacía del servidor")
            } else {
                Resource.Error("HTTP ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error de red")
        }
    }

    suspend fun getUsuario(id: Int): Resource<LoginResponse> {
        return try {
            val response = literaVerseApi.getUsuario(id)
            if (response.isSuccessful) {
                response.body()?.let { Resource.Success(it) }
                    ?: Resource.Error("Respuesta vacía del servidor")
            } else {
                Resource.Error("HTTP ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error de red")
        }
    }

    suspend fun updateUsuario(id: Int, request: LoginRequest): Resource<Unit> {
        return try {
            val response = literaVerseApi.updateUsuario(id, request)
            if (response.isSuccessful) {
                Resource.Success(Unit)
            } else {
                Resource.Error("HTTP ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error de red")
        }
    }

    suspend fun getFeaturedStories(): Resource<List<StoryResponse>> {
        return try {
            val response = literaVerseApi.getFeaturedStories()
            if (response.isSuccessful) {
                response.body()?.let { Resource.Success(it) }
                    ?: Resource.Error("Respuesta vacía del servidor")
            } else {
                Resource.Error("HTTP ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error de red")
        }
    }

    suspend fun getPopularStories(): Resource<List<StoryResponse>> {
        return try {
            val response = literaVerseApi.getPopularStories()
            if (response.isSuccessful) {
                response.body()?.let { Resource.Success(it) }
                    ?: Resource.Error("Respuesta vacía del servidor")
            } else {
                Resource.Error("HTTP ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error de red")
        }
    }

    suspend fun getNewStories(): Resource<List<StoryResponse>> {
        return try {
            val response = literaVerseApi.getNewStories()
            if (response.isSuccessful) {
                response.body()?.let { Resource.Success(it) }
                    ?: Resource.Error("Respuesta vacía del servidor")
            } else {
                Resource.Error("HTTP ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error de red")
        }
    }

    suspend fun getGenres(): Resource<List<GenreResponse>> {
        return try {
            val response = literaVerseApi.getGenres()
            if (response.isSuccessful) {
                response.body()?.let { Resource.Success(it) }
                    ?: Resource.Error("Respuesta vacía del servidor")
            } else {
                Resource.Error("HTTP ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error de red")
        }
    }

    suspend fun getStoriesByGenre(genreName: String): Resource<List<StoryResponse>> {
        return try {
            val response = literaVerseApi.getStoriesByGenre(genreName)
            if (response.isSuccessful) {
                response.body()?.let { Resource.Success(it) }
                    ?: Resource.Error("Respuesta vacía del servidor")
            } else {
                Resource.Error("HTTP ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error de red")
        }
    }
}