package edu.ucne.literaverse.data.repository

import edu.ucne.literaverse.data.local.dao.UsuarioDao
import edu.ucne.literaverse.data.remote.RemoteDataSource
import edu.ucne.literaverse.data.remote.Resource
import edu.ucne.literaverse.data.remote.dto.LoginRequest
import edu.ucne.literaverse.data.remote.dto.LoginResponse
import edu.ucne.literaverse.data.remote.dto.RegisterRequest
import edu.ucne.literaverse.data.remote.dto.ValidateTokenResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class UsuarioRepositoryImplTest {

    private lateinit var repository: UsuarioRepositoryImpl
    private lateinit var localDataSource: UsuarioDao
    private lateinit var remoteDataSource: RemoteDataSource

    @Before
    fun setup() {
        localDataSource = mockk(relaxed = true)
        remoteDataSource = mockk()
        repository = UsuarioRepositoryImpl(localDataSource, remoteDataSource)
    }

    @Test
    fun `login retorna Success cuando credenciales son correctas`() = runTest {
        // Given
        val userName = "testUser"
        val password = "testPass"
        val mockResponse = LoginResponse(
            userId = 1,
            userName = userName,
            token = "fake-token-123",
            loginDate = "2024-12-03"
        )

        coEvery {
            remoteDataSource.login(any())
        } returns Resource.Success(mockResponse)

        // When
        val result = repository.login(userName, password)

        // Then
        assertTrue(result is Resource.Success)
        assertEquals(userName, (result as Resource.Success).data?.userName)
        coVerify { remoteDataSource.login(any()) }
    }

    @Test
    fun `login retorna Error cuando credenciales son incorrectas`() = runTest {
        // Given
        val userName = "wrongUser"
        val password = "wrongPass"

        coEvery {
            remoteDataSource.login(any())
        } returns Resource.Error("Credenciales inválidas")

        // When
        val result = repository.login(userName, password)

        // Then
        assertTrue(result is Resource.Error)
        assertNotNull((result as Resource.Error).message)
    }

    @Test
    fun `register retorna Success cuando usuario se crea correctamente`() = runTest {
        // Given
        val userName = "newUser"
        val password = "newPass123"
        val mockResponse = LoginResponse(
            userId = 2,
            userName = userName,
            token = "new-token-456",
            loginDate = "2024-12-03"
        )

        coEvery {
            remoteDataSource.register(any())
        } returns Resource.Success(mockResponse)

        // When
        val result = repository.register(userName, password)

        // Then
        assertTrue(result is Resource.Success)
        assertEquals(userName, (result as Resource.Success).data?.userName)
        coVerify { remoteDataSource.register(any()) }
    }

    @Test
    fun `register retorna Error cuando usuario ya existe`() = runTest {
        // Given
        val userName = "existingUser"
        val password = "pass123"

        coEvery {
            remoteDataSource.register(any())
        } returns Resource.Error("Usuario ya existe")

        // When
        val result = repository.register(userName, password)

        // Then
        assertTrue(result is Resource.Error)
    }

    @Test
    fun `validateToken retorna Success cuando token es válido`() = runTest {
        // Given
        val token = "valid-token"
        val mockResponse = ValidateTokenResponse(
            isValid = true,
            userId = 1,
            message = "Token válido"
        )

        coEvery {
            remoteDataSource.validateToken(token)
        } returns Resource.Success(mockResponse)

        // When
        val result = repository.validateToken(token)

        // Then
        assertTrue(result is Resource.Success)
        assertTrue((result as Resource.Success).data == true)
    }

    @Test
    fun `validateToken retorna Error cuando token es inválido`() = runTest {
        // Given
        val token = "invalid-token"

        coEvery {
            remoteDataSource.validateToken(token)
        } returns Resource.Error("Token inválido")

        // When
        val result = repository.validateToken(token)

        // Then
        assertTrue(result is Resource.Error)
    }

    @Test
    fun `logout llama al remoteDataSource correctamente`() = runTest {
        // Given
        val token = "user-token"

        coEvery {
            remoteDataSource.logout(token)
        } returns Resource.Success(Unit)

        // When
        val result = repository.logout(token)

        // Then
        assertTrue(result is Resource.Success)
        coVerify { remoteDataSource.logout(token) }
    }
}