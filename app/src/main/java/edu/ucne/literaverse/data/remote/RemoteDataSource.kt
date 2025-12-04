package edu.ucne.literaverse.data.remote

import edu.ucne.literaverse.data.remote.dto.ChapterResponse
import edu.ucne.literaverse.data.remote.dto.CreateChapterRequest
import edu.ucne.literaverse.data.remote.dto.CreateStoryRequest
import edu.ucne.literaverse.data.remote.dto.LoginRequest
import edu.ucne.literaverse.data.remote.dto.LoginResponse
import edu.ucne.literaverse.data.remote.dto.RegisterRequest
import edu.ucne.literaverse.data.remote.dto.ReadingProgressRequest
import edu.ucne.literaverse.data.remote.dto.ReadingProgressResponse
import edu.ucne.literaverse.data.remote.dto.GenreResponse
import edu.ucne.literaverse.data.remote.dto.StoryDetailResponse
import edu.ucne.literaverse.data.remote.dto.StoryResponse
import edu.ucne.literaverse.data.remote.dto.SessionResponse
import edu.ucne.literaverse.data.remote.dto.UserProfileResponse
import edu.ucne.literaverse.data.remote.dto.UpdateChapterRequest
import edu.ucne.literaverse.data.remote.dto.UpdateStoryRequest
import edu.ucne.literaverse.data.remote.dto.ValidateTokenResponse
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val literaVerseApi: LiteraVerseApi
) {
    private companion object {
        const val EMPTY_RESPONSE_ERROR = "Respuesta vacía del servidor"
    }

    suspend fun login(request: LoginRequest): Resource<LoginResponse> {
        return try {
            val response = literaVerseApi.login(request)
            if (response.isSuccessful) {
                response.body()?.let { Resource.Success(it) }
                    ?: Resource.Error(EMPTY_RESPONSE_ERROR)
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
                    ?: Resource.Error(EMPTY_RESPONSE_ERROR)
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

    suspend fun validateToken(token: String): Resource<ValidateTokenResponse> {
        return try {
            val response = literaVerseApi.validateToken(token)
            if (response.isSuccessful) {
                response.body()?.let { Resource.Success(it) }
                    ?: Resource.Error(EMPTY_RESPONSE_ERROR)
            } else {
                Resource.Error("Token inválido")
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
                    ?: Resource.Error(EMPTY_RESPONSE_ERROR)
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
                    ?: Resource.Error(EMPTY_RESPONSE_ERROR)
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
                    ?: Resource.Error(EMPTY_RESPONSE_ERROR)
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
                    ?: Resource.Error(EMPTY_RESPONSE_ERROR)
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
                    ?: Resource.Error(EMPTY_RESPONSE_ERROR)
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
                    ?: Resource.Error(EMPTY_RESPONSE_ERROR)
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
                    ?: Resource.Error(EMPTY_RESPONSE_ERROR)
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
                    ?: Resource.Error(EMPTY_RESPONSE_ERROR)
            } else {
                Resource.Error("HTTP ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error de red")
        }
    }

    suspend fun getStoryForReader(storyId: Int): Resource<Pair<StoryDetailResponse, List<ChapterResponse>>> {
        return try {
            val storyResponse = literaVerseApi.getStoryById(storyId)
            if (!storyResponse.isSuccessful) {
                return Resource.Error("HTTP ${storyResponse.code()} ${storyResponse.message()}")
            }

            val story = storyResponse.body() ?: return Resource.Error("Historia no encontrada")

            val chaptersResponse = literaVerseApi.getChaptersByStory(storyId)
            if (!chaptersResponse.isSuccessful) {
                return Resource.Error("HTTP ${chaptersResponse.code()} ${chaptersResponse.message()}")
            }

            val chapters = chaptersResponse.body() ?: emptyList()
            Resource.Success(Pair(story, chapters))
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error de red")
        }
    }

    suspend fun updateStory(storyId: Int, request: UpdateStoryRequest): Resource<StoryResponse> {
        return try {
            val response = literaVerseApi.updateStory(storyId, request)
            if (response.isSuccessful) {
                response.body()?.let { Resource.Success(it) }
                    ?: Resource.Error(EMPTY_RESPONSE_ERROR)
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
                    ?: Resource.Error(EMPTY_RESPONSE_ERROR)
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
                    ?: Resource.Error(EMPTY_RESPONSE_ERROR)
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
                    ?: Resource.Error(EMPTY_RESPONSE_ERROR)
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
                    ?: Resource.Error(EMPTY_RESPONSE_ERROR)
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

    suspend fun getFavorites(userId: Int): Resource<List<StoryResponse>> {
        return try {
            val response = literaVerseApi.getFavorites(userId)
            if (response.isSuccessful) {
                response.body()?.let { Resource.Success(it) }
                    ?: Resource.Error(EMPTY_RESPONSE_ERROR)
            } else {
                Resource.Error("HTTP ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error de red")
        }
    }

    suspend fun addFavorite(userId: Int, storyId: Int): Resource<Unit> {
        return try {
            val response = literaVerseApi.addFavorite(userId, storyId)
            if (response.isSuccessful) {
                Resource.Success(Unit)
            } else {
                Resource.Error("HTTP ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error de red")
        }
    }

    suspend fun removeFavorite(userId: Int, storyId: Int): Resource<Unit> {
        return try {
            val response = literaVerseApi.removeFavorite(userId, storyId)
            if (response.isSuccessful) {
                Resource.Success(Unit)
            } else {
                Resource.Error("HTTP ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error de red")
        }
    }

    suspend fun isFavorite(userId: Int, storyId: Int): Resource<Boolean> {
        return try {
            val response = literaVerseApi.isFavorite(userId, storyId)
            if (response.isSuccessful) {
                val isFav = response.body()?.get("isFavorite") as? Boolean ?: false
                Resource.Success(isFav)
            } else {
                Resource.Error("HTTP ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error de red")
        }
    }

    suspend fun getReadingProgress(userId: Int): Resource<List<ReadingProgressResponse>> {
        return try {
            val response = literaVerseApi.getReadingProgress(userId)
            if (response.isSuccessful) {
                response.body()?.let { Resource.Success(it) }
                    ?: Resource.Error(EMPTY_RESPONSE_ERROR)
            } else {
                Resource.Error("HTTP ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error de red")
        }
    }

    suspend fun saveReadingProgress(request: ReadingProgressRequest): Resource<Unit> {
        return try {
            val response = literaVerseApi.saveReadingProgress(request)
            if (response.isSuccessful) {
                Resource.Success(Unit)
            } else {
                Resource.Error("HTTP ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error de red")
        }
    }

    suspend fun getCompletedStories(userId: Int): Resource<List<StoryResponse>> {
        return try {
            val response = literaVerseApi.getCompletedStories(userId)
            if (response.isSuccessful) {
                response.body()?.let { Resource.Success(it) }
                    ?: Resource.Error(EMPTY_RESPONSE_ERROR)
            } else {
                Resource.Error("HTTP ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error de red")
        }
    }

    suspend fun getUserProfile(userId: Int): Resource<UserProfileResponse> {
        return try {
            val response = literaVerseApi.getUserProfile(userId)
            if (response.isSuccessful) {
                response.body()?.let { Resource.Success(it) }
                    ?: Resource.Error(EMPTY_RESPONSE_ERROR)
            } else {
                Resource.Error("HTTP ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error de red")
        }
    }

    suspend fun getUserSessions(userId: Int): Resource<List<SessionResponse>> {
        return try {
            val response = literaVerseApi.getUserSessions(userId)
            if (response.isSuccessful) {
                response.body()?.let { Resource.Success(it) }
                    ?: Resource.Error(EMPTY_RESPONSE_ERROR)
            } else {
                Resource.Error("HTTP ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error de red")
        }
    }

    suspend fun logoutAllSessions(userId: Int): Resource<Unit> {
        return try {
            val response = literaVerseApi.logoutAllSessions(userId)
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


