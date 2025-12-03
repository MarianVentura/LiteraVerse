package edu.ucne.literaverse.data.repository

import edu.ucne.literaverse.data.local.dao.ChapterDao
import edu.ucne.literaverse.data.local.dao.StoryDao
import edu.ucne.literaverse.data.local.entities.StoryEntity
import edu.ucne.literaverse.data.remote.RemoteDataSource
import edu.ucne.literaverse.data.remote.Resource
import edu.ucne.literaverse.data.remote.dto.ReadingProgressRequest
import edu.ucne.literaverse.data.remote.dto.ReadingProgressResponse
import edu.ucne.literaverse.data.remote.dto.StoryResponse
import io.mockk.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class LibraryRepositoryImplTest {

    private lateinit var repository: LibraryRepositoryImpl
    private lateinit var remoteDataSource: RemoteDataSource
    private lateinit var storyDao: StoryDao
    private lateinit var chapterDao: ChapterDao

    @Before
    fun setup() {
        remoteDataSource = mockk()
        storyDao = mockk(relaxed = true)
        chapterDao = mockk(relaxed = true)
        repository = LibraryRepositoryImpl(remoteDataSource, storyDao, chapterDao)
    }

    @Test
    fun `addFavorite actualiza estado local y llama a remoteDataSource`() = runTest {

        val userId = 1
        val storyId = 10

        coEvery {
            remoteDataSource.addFavorite(userId, storyId)
        } returns Resource.Success(Unit)


        val result = repository.addFavorite(userId, storyId)


        assertTrue(result is Resource.Success)
        coVerify { storyDao.updateFavoriteStatus(storyId, true) }
        coVerify { remoteDataSource.addFavorite(userId, storyId) }
    }

    @Test
    fun `addFavorite revierte cambio local cuando falla el remoto`() = runTest {

        val userId = 1
        val storyId = 10

        coEvery {
            remoteDataSource.addFavorite(userId, storyId)
        } returns Resource.Error("Error de red")


        val result = repository.addFavorite(userId, storyId)


        assertTrue(result is Resource.Error)

        coVerify { storyDao.updateFavoriteStatus(storyId, true) }

        coVerify { storyDao.updateFavoriteStatus(storyId, false) }
    }

    @Test
    fun `removeFavorite actualiza estado local y llama a remoteDataSource`() = runTest {

        val userId = 1
        val storyId = 10

        coEvery {
            remoteDataSource.removeFavorite(userId, storyId)
        } returns Resource.Success(Unit)


        val result = repository.removeFavorite(userId, storyId)


        assertTrue(result is Resource.Success)
        coVerify { storyDao.updateFavoriteStatus(storyId, false) }
        coVerify { remoteDataSource.removeFavorite(userId, storyId) }
    }

    @Test
    fun `removeFavorite revierte cambio local cuando falla el remoto`() = runTest {

        val userId = 1
        val storyId = 10

        coEvery {
            remoteDataSource.removeFavorite(userId, storyId)
        } returns Resource.Error("Error de red")


        val result = repository.removeFavorite(userId, storyId)


        assertTrue(result is Resource.Error)
        coVerify { storyDao.updateFavoriteStatus(storyId, false) }
        coVerify { storyDao.updateFavoriteStatus(storyId, true) }
    }

    @Test
    fun `isFavorite llama al remoteDataSource correctamente`() = runTest {

        val userId = 1
        val storyId = 10

        coEvery {
            remoteDataSource.isFavorite(userId, storyId)
        } returns Resource.Success(true)


        val result = repository.isFavorite(userId, storyId)


        assertTrue(result is Resource.Success)
        assertEquals(true, (result as Resource.Success).data)
        coVerify { remoteDataSource.isFavorite(userId, storyId) }
    }

    @Test
    fun `saveReadingProgress actualiza base de datos local y remota`() = runTest {

        val userId = 1
        val storyId = 10
        val chapterId = 5
        val scrollPosition = 0.75

        coEvery {
            remoteDataSource.saveReadingProgress(any())
        } returns Resource.Success(Unit)


        val result = repository.saveReadingProgress(userId, storyId, chapterId, scrollPosition)


        assertTrue(result is Resource.Success)
        coVerify {
            storyDao.updateReadingProgress(
                storyId = storyId,
                chapterId = chapterId,
                scrollPosition = scrollPosition,
                lastReadAt = any()
            )
        }
        coVerify { remoteDataSource.saveReadingProgress(any()) }
    }

    @Test
    fun `saveReadingProgress retorna Error cuando falla remoto`() = runTest {

        val userId = 1
        val storyId = 10
        val chapterId = 5
        val scrollPosition = 0.5

        coEvery {
            remoteDataSource.saveReadingProgress(any())
        } returns Resource.Error("Error al guardar")


        val result = repository.saveReadingProgress(userId, storyId, chapterId, scrollPosition)


        assertTrue(result is Resource.Error)

        coVerify { storyDao.updateReadingProgress(any(), any(), any(), any()) }
    }

    @Test
    fun `markAsCompleted actualiza estados correctamente`() = runTest {

        val userId = 1
        val storyId = 10


        val result = repository.markAsCompleted(userId, storyId)


        assertTrue(result is Resource.Success)
        coVerify { storyDao.updateCompletedStatus(storyId, true) }
        coVerify { storyDao.updateReadingStatus(storyId, false) }
    }

    @Test
    fun `updateReadingStatus actualiza estado correctamente`() = runTest {

        val userId = 1
        val storyId = 10
        val isReading = true


        val result = repository.updateReadingStatus(userId, storyId, isReading)


        assertTrue(result is Resource.Success)
        coVerify { storyDao.updateReadingStatus(storyId, isReading) }
    }

    @Test
    fun `syncLibrary sincroniza favoritos desde remoto`() = runTest {
        
        val userId = 1
        val mockStories = listOf(
            StoryResponse(
                storyId = 1,
                userId = 1,
                userName = "Author1",
                title = "Story 1",
                synopsis = "Synopsis",
                coverImageUrl = null,
                genre = "Fantasy",
                tags = null,
                viewCount = 100,
                createdAt = "2024-01-01",
                isDraft = false,
                isPublished = true,
                publishedAt = "2024-01-01",
                updatedAt = "2024-01-01"
            )
        )

        coEvery {
            remoteDataSource.getFavorites(userId)
        } returns Resource.Success(mockStories)

        coEvery {
            remoteDataSource.getReadingProgress(userId)
        } returns Resource.Success(emptyList())

        coEvery {
            remoteDataSource.getCompletedStories(userId)
        } returns Resource.Success(emptyList())

        // When
        repository.syncLibrary(userId)

        // Then
        coVerify { remoteDataSource.getFavorites(userId) }
        coVerify { storyDao.upsert(any()) }
    }

    @Test
    fun `syncLibrary sincroniza progreso de lectura`() = runTest {
        // Given
        val userId = 1
        val mockProgress = listOf(
            ReadingProgressResponse(
                progressId = 1,
                userId = userId,
                storyId = 1,
                chapterId = 2,
                scrollPosition = 0.5,
                lastReadAt = "2024-12-03T10:30:00",
                storyTitle = "Story Title",
                chapterTitle = "Chapter Title"
            )
        )

        val mockStoryEntity = StoryEntity(
            storyId = 1,
            userId = 1,
            userName = "Author",
            title = "Story",
            synopsis = "Synopsis",
            coverImageUrl = null,
            genre = "Fantasy",
            tags = null,
            viewCount = 100,
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
            remoteDataSource.getFavorites(userId)
        } returns Resource.Success(emptyList())

        coEvery {
            remoteDataSource.getReadingProgress(userId)
        } returns Resource.Success(mockProgress)

        coEvery {
            remoteDataSource.getCompletedStories(userId)
        } returns Resource.Success(emptyList())

        coEvery {
            storyDao.getStoryById(1)
        } returns mockStoryEntity

        // When
        repository.syncLibrary(userId)

        // Then
        coVerify { remoteDataSource.getReadingProgress(userId) }
        coVerify { storyDao.updateReadingProgress(1, 2, 0.5, any()) }
    }
}