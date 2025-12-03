package edu.ucne.literaverse.data.repository

import edu.ucne.literaverse.data.local.dao.StoryDao
import edu.ucne.literaverse.data.local.entities.StoryEntity
import edu.ucne.literaverse.data.remote.RemoteDataSource
import edu.ucne.literaverse.data.remote.Resource
import edu.ucne.literaverse.data.remote.dto.CreateStoryRequest
import edu.ucne.literaverse.data.remote.dto.StoryDetailResponse
import edu.ucne.literaverse.data.remote.dto.StoryResponse
import edu.ucne.literaverse.data.remote.dto.UpdateStoryRequest
import edu.ucne.literaverse.domain.model.StoryDetail
import io.mockk.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class StoryRepositoryImplTest {

    private lateinit var repository: StoryRepositoryImpl
    private lateinit var remoteDataSource: RemoteDataSource
    private lateinit var storyDao: StoryDao

    @Before
    fun setup() {
        remoteDataSource = mockk()
        storyDao = mockk(relaxed = true)
        repository = StoryRepositoryImpl(remoteDataSource, storyDao)
    }

    @Test
    fun `createStory retorna Success cuando se crea correctamente`() = runTest {
        // Given
        val userId = 1
        val title = "New Story"
        val synopsis = "Synopsis"
        val genre = "Fantasy"
        val tags = "adventure,magic"

        val mockResponse = StoryResponse(
            storyId = 10,
            userId = userId,
            userName = "Author",
            title = title,
            synopsis = synopsis,
            coverImageUrl = null,
            genre = genre,
            tags = tags,
            isDraft = true,
            isPublished = false,
            createdAt = "2024-01-01",
            publishedAt = null,
            updatedAt = "2024-01-01",
            viewCount = 0
        )

        coEvery {
            remoteDataSource.createStory(any())
        } returns Resource.Success(mockResponse)

        // When
        val result = repository.createStory(userId, title, synopsis, genre, tags)

        // Then
        assertTrue(result is Resource.Success)
        val storyDetail = (result as Resource.Success).data
        assertNotNull(storyDetail)
        assertEquals(title, storyDetail?.title)
        assertEquals(10, storyDetail?.storyId)
        coVerify { remoteDataSource.createStory(any()) }
        coVerify { storyDao.upsert(any()) }
    }

    @Test
    fun `createStory retorna Error cuando falla la creación`() = runTest {
        // Given
        val userId = 1
        val title = "New Story"
        val synopsis = "Synopsis"
        val genre = "Fantasy"

        coEvery {
            remoteDataSource.createStory(any())
        } returns Resource.Error("Error al crear historia")

        // When
        val result = repository.createStory(userId, title, synopsis, genre, null)

        // Then
        assertTrue(result is Resource.Error)
        assertNotNull((result as Resource.Error).message)
    }

    @Test
    fun `getStoryById retorna Success desde remoto y guarda en local`() = runTest {
        // Given
        val storyId = 10
        val mockResponse = StoryDetailResponse(
            storyId = storyId,
            userId = 1,
            userName = "Author",
            title = "Test Story",
            synopsis = "Synopsis",
            coverImageUrl = null,
            genre = "Fantasy",
            tags = null,
            isDraft = false,
            isPublished = true,
            createdAt = "2024-01-01",
            publishedAt = "2024-01-02",
            updatedAt = "2024-01-01",
            viewCount = 100
        )

        coEvery {
            remoteDataSource.getStoryById(storyId)
        } returns Resource.Success(mockResponse)

        // When
        val result = repository.getStoryById(storyId)

        // Then
        assertTrue(result is Resource.Success)
        val storyDetail = (result as Resource.Success).data
        assertEquals(storyId, storyDetail?.storyId)
        coVerify { remoteDataSource.getStoryById(storyId) }
        coVerify { storyDao.upsert(any()) }
    }

    @Test
    fun `getStoryById retorna desde local cuando falla remoto`() = runTest {
        // Given
        val storyId = 10
        val mockEntity = StoryEntity(
            storyId = storyId,
            userId = 1,
            userName = "Author",
            title = "Local Story",
            synopsis = "Synopsis",
            coverImageUrl = null,
            genre = "Fantasy",
            tags = null,
            viewCount = 50,
            createdAt = "2024-01-01",
            updatedAt = "2024-01-01",
            isDraft = false,
            isPublished = true,
            publishedAt = "2024-01-01",
            isFavorite = false,
            isReading = false,
            isCompleted = false,
            lastReadChapterId = null,
            scrollPosition = 0.0,
            lastReadAt = null,
            needsSync = false
        )

        coEvery {
            remoteDataSource.getStoryById(storyId)
        } returns Resource.Error("Network error")

        coEvery {
            storyDao.getStoryById(storyId)
        } returns mockEntity

        // When
        val result = repository.getStoryById(storyId)

        // Then
        assertTrue(result is Resource.Success)
        val storyDetail = (result as Resource.Success).data
        assertEquals("Local Story", storyDetail?.title)
    }

    @Test
    fun `getStoryById retorna Error cuando no hay datos locales ni remotos`() = runTest {
        // Given
        val storyId = 10

        coEvery {
            remoteDataSource.getStoryById(storyId)
        } returns Resource.Error("Not found")

        coEvery {
            storyDao.getStoryById(storyId)
        } returns null

        // When
        val result = repository.getStoryById(storyId)

        // Then
        assertTrue(result is Resource.Error)
    }

    @Test
    fun `updateStory actualiza historia correctamente`() = runTest {
        // Given
        val storyId = 10
        val title = "Updated Title"
        val synopsis = "Updated Synopsis"
        val genre = "Romance"
        val tags = "love,drama"

        val mockResponse = StoryResponse(
            storyId = storyId,
            userId = 1,
            userName = "Author",
            title = title,
            synopsis = synopsis,
            coverImageUrl = null,
            genre = genre,
            tags = tags,
            isDraft = false,
            isPublished = true,
            createdAt = "2024-01-01",
            publishedAt = "2024-01-02",
            updatedAt = "2024-01-03",
            viewCount = 100
        )

        coEvery {
            remoteDataSource.updateStory(storyId, any())
        } returns Resource.Success(mockResponse)

        // When
        val result = repository.updateStory(storyId, title, synopsis, genre, tags)

        // Then
        assertTrue(result is Resource.Success)
        val storyDetail = (result as Resource.Success).data
        assertEquals(title, storyDetail?.title)
        coVerify { remoteDataSource.updateStory(storyId, any()) }
        coVerify { storyDao.upsert(any()) }
    }

    @Test
    fun `deleteStory elimina de remoto y local`() = runTest {
        // Given
        val storyId = 10

        coEvery {
            remoteDataSource.deleteStory(storyId)
        } returns Resource.Success(Unit)

        // When
        val result = repository.deleteStory(storyId)

        // Then
        assertTrue(result is Resource.Success)
        coVerify { remoteDataSource.deleteStory(storyId) }
        coVerify { storyDao.delete(storyId) }
    }

    @Test
    fun `deleteStory retorna Error cuando falla`() = runTest {
        // Given
        val storyId = 10

        coEvery {
            remoteDataSource.deleteStory(storyId)
        } returns Resource.Error("No se puede eliminar")

        // When
        val result = repository.deleteStory(storyId)

        // Then
        assertTrue(result is Resource.Error)
        coVerify(exactly = 0) { storyDao.delete(any()) }
    }

    @Test
    fun `publishStory actualiza estado correctamente`() = runTest {
        // Given
        val storyId = 10
        val mockEntity = StoryEntity(
            storyId = storyId,
            userId = 1,
            userName = "Author",
            title = "Story",
            synopsis = "Synopsis",
            coverImageUrl = null,
            genre = "Fantasy",
            tags = null,
            viewCount = 0,
            createdAt = "2024-01-01",
            updatedAt = "2024-01-01",
            isDraft = true,
            isPublished = false,
            publishedAt = null,
            isFavorite = false,
            isReading = false,
            isCompleted = false,
            lastReadChapterId = null,
            scrollPosition = 0.0,
            lastReadAt = null,
            needsSync = false
        )

        coEvery {
            remoteDataSource.publishStory(storyId)
        } returns Resource.Success(Unit)

        coEvery {
            storyDao.getStoryById(storyId)
        } returns mockEntity

        // When
        val result = repository.publishStory(storyId)

        // Then
        assertTrue(result is Resource.Success)
        coVerify { remoteDataSource.publishStory(storyId) }
        coVerify {
            storyDao.upsert(match<StoryEntity> {
                it.isPublished == true && it.isDraft == false
            })
        }
    }

    @Test
    fun `unpublishStory actualiza estado correctamente`() = runTest {
        // Given
        val storyId = 10
        val mockEntity = StoryEntity(
            storyId = storyId,
            userId = 1,
            userName = "Author",
            title = "Story",
            synopsis = "Synopsis",
            coverImageUrl = null,
            genre = "Fantasy",
            tags = null,
            viewCount = 0,
            createdAt = "2024-01-01",
            updatedAt = "2024-01-01",
            isDraft = false,
            isPublished = true,
            publishedAt = "2024-01-01",
            isFavorite = false,
            isReading = false,
            isCompleted = false,
            lastReadChapterId = null,
            scrollPosition = 0.0,
            lastReadAt = null,
            needsSync = false
        )

        coEvery {
            remoteDataSource.unpublishStory(storyId)
        } returns Resource.Success(Unit)

        coEvery {
            storyDao.getStoryById(storyId)
        } returns mockEntity

        // When
        val result = repository.unpublishStory(storyId)

        // Then
        assertTrue(result is Resource.Success)
        coVerify { remoteDataSource.unpublishStory(storyId) }
        coVerify {
            storyDao.upsert(match<StoryEntity> {
                it.isPublished == false && it.isDraft == true
            })
        }
    }
}