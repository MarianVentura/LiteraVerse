package edu.ucne.literaverse.data.remote

import edu.ucne.literaverse.data.remote.dto.LoginRequest
import edu.ucne.literaverse.data.remote.dto.LoginResponse
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
}