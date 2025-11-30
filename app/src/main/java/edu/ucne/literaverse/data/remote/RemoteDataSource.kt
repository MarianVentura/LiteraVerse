package edu.ucne.literaverse.data.remote

import edu.ucne.literaverse.data.remote.dto.ChapterResponse
import edu.ucne.literaverse.data.remote.dto.CreateChapterRequest
import edu.ucne.literaverse.data.remote.dto.CreateStoryRequest
import edu.ucne.literaverse.data.remote.dto.LoginRequest
import edu.ucne.literaverse.data.remote.dto.LoginResponse
import edu.ucne.literaverse.data.remote.dto.RegisterRequest
import edu.ucne.literaverse.data.remote.dto.GenreResponse
import edu.ucne.literaverse.data.remote.dto.StoryDetailResponse
import edu.ucne.literaverse.data.remote.dto.StoryResponse
import edu.ucne.literaverse.data.remote.dto.UpdateChapterRequest
import edu.ucne.literaverse.data.remote.dto.UpdateStoryRequest
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val literaVerseApi: LiteraVerseApi
) {
    suspend fun login(request: LoginRequest): Resource<LoginResponse> {
        return try {
            val response = literaVerseApi.login(request)
            if (response.isSuccessful) {
                response.body()?.let { Resource.Success(it) }
                    ?: Resource.Error("Respuesta vacía del servidor")
            } else {
                when (response.code()) {
                    401 -> Resource.Error("Usuario o contraseña incorrectos")
                    else -> Resource.Error("HTTP ${response.code()} ${response.message()}")
                }
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error de red")
        }
    }

    suspend fun register(request: RegisterRequest): Resource<LoginResponse> {
        return try {
            val response = literaVerseApi.register(request)
            if (response.isSuccessful) {
                response.body()?.let { Resource.Success(it) }
                    ?: Resource.Error("Respuesta vacía del servidor")
            } else {
                when (response.code()) {
                    400 -> Resource.Error("El usuario ya existe")
                    else -> Resource.Error("HTTP ${response.code()} ${response.message()}")
                }
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error de red")
        }
    }

    suspend fun logout(token: String): Resource<Unit> {
        return try {
            val response = literaVerseApi.logout(token)
            if (response.isSuccessful) {
                Resource.Success(Unit)
            } else {
                Resource.Error("HTTP ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error de red")
        }
    }

    suspend fun validateToken(token: String): Resource<Map<String, Any>> {
        return try {
            val response = literaVerseApi.validateToken(token)
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

    suspend fun createStory(request: CreateStoryRequest): Resource<StoryResponse> {
        return try {
            val response = literaVerseApi.createStory(request)
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

    suspend fun getStoriesByUser(userId: Int): Resource<List<StoryResponse>> {
        return try {
            val response = literaVerseApi.getStoriesByUser(userId)
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

    suspend fun getStoryById(storyId: Int): Resource<StoryDetailResponse> {
        return try {
            val response = literaVerseApi.getStoryById(storyId)
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

    suspend fun updateStory(storyId: Int, request: UpdateStoryRequest): Resource<StoryResponse> {
        return try {
            val response = literaVerseApi.updateStory(storyId, request)
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

    suspend fun deleteStory(storyId: Int): Resource<Unit> {
        return try {
            val response = literaVerseApi.deleteStory(storyId)
            if (response.isSuccessful) {
                Resource.Success(Unit)
            } else {
                Resource.Error("HTTP ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error de red")
        }
    }

    suspend fun publishStory(storyId: Int): Resource<Unit> {
        return try {
            val response = literaVerseApi.publishStory(storyId)
            if (response.isSuccessful) {
                Resource.Success(Unit)
            } else {
                Resource.Error("HTTP ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error de red")
        }
    }

    suspend fun unpublishStory(storyId: Int): Resource<Unit> {
        return try {
            val response = literaVerseApi.unpublishStory(storyId)
            if (response.isSuccessful) {
                Resource.Success(Unit)
            } else {
                Resource.Error("HTTP ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error de red")
        }
    }

    suspend fun getChaptersByStory(storyId: Int): Resource<List<ChapterResponse>> {
        return try {
            val response = literaVerseApi.getChaptersByStory(storyId)
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

    suspend fun createChapter(storyId: Int, request: CreateChapterRequest): Resource<ChapterResponse> {
        return try {
            val response = literaVerseApi.createChapter(storyId, request)
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

    suspend fun getChapterById(storyId: Int, chapterId: Int): Resource<ChapterResponse> {
        return try {
            val response = literaVerseApi.getChapterById(storyId, chapterId)
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

    suspend fun updateChapter(storyId: Int, chapterId: Int, request: UpdateChapterRequest): Resource<ChapterResponse> {
        return try {
            val response = literaVerseApi.updateChapter(storyId, chapterId, request)
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

    suspend fun deleteChapter(storyId: Int, chapterId: Int): Resource<Unit> {
        return try {
            val response = literaVerseApi.deleteChapter(storyId, chapterId)
            if (response.isSuccessful) {
                Resource.Success(Unit)
            } else {
                Resource.Error("HTTP ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error de red")
        }
    }

    suspend fun publishChapter(storyId: Int, chapterId: Int): Resource<Unit> {
        return try {
            val response = literaVerseApi.publishChapter(storyId, chapterId)
            if (response.isSuccessful) {
                Resource.Success(Unit)
            } else {
                Resource.Error("HTTP ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error de red")
        }
    }

    suspend fun unpublishChapter(storyId: Int, chapterId: Int): Resource<Unit> {
        return try {
            val response = literaVerseApi.unpublishChapter(storyId, chapterId)
            if (response.isSuccessful) {
                Resource.Success(Unit)
            } else {
                Resource.Error("HTTP ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error de red")
        }
    }
}
