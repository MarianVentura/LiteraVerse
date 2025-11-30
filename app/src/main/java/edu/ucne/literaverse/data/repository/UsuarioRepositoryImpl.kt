package edu.ucne.literaverse.data.repository

import edu.ucne.literaverse.data.local.dao.UsuarioDao
import edu.ucne.literaverse.data.mappers.toDomain
import edu.ucne.literaverse.data.mappers.toLoginRequest
import edu.ucne.literaverse.data.mappers.toRegisterRequest
import edu.ucne.literaverse.data.remote.RemoteDataSource
import edu.ucne.literaverse.data.remote.Resource
import edu.ucne.literaverse.domain.model.Usuario
import edu.ucne.literaverse.domain.repository.UsuarioRepository
import javax.inject.Inject

class UsuarioRepositoryImpl @Inject constructor(
    private val localDataSource: UsuarioDao,
    private val remoteDataSource: RemoteDataSource
) : UsuarioRepository {

    override suspend fun login(userName: String, password: String): Resource<Usuario> {
        val usuario = Usuario(
            usuarioId = 0,
            userName = userName,
            password = password
        )

        val request = usuario.toLoginRequest()

        return when (val result = remoteDataSource.login(request)) {
            is Resource.Success -> {
                result.data?.let { Resource.Success(it.toDomain()) }
                    ?: Resource.Error("Usuario no encontrado")
            }
            is Resource.Error -> Resource.Error(result.message ?: "Error al iniciar sesión")
            is Resource.Loading -> Resource.Loading()
        }
    }

    override suspend fun register(userName: String, password: String): Resource<Usuario> {
        val usuario = Usuario(
            usuarioId = 0,
            userName = userName,
            password = password
        )

        val request = usuario.toRegisterRequest()

        return when (val result = remoteDataSource.register(request)) {
            is Resource.Success -> {
                result.data?.let { Resource.Success(it.toDomain()) }
                    ?: Resource.Error("Error al crear usuario")
            }
            is Resource.Error -> Resource.Error(result.message ?: "Error al registrar usuario")
            is Resource.Loading -> Resource.Loading()
        }
    }

    override suspend fun getUsuarios(): Resource<List<Usuario>> {
        return Resource.Error("Método no disponible")
    }

    override suspend fun validateToken(token: String): Resource<Boolean> {
        return when (val result = remoteDataSource.validateToken(token)) {
            is Resource.Success -> {
                if (result.data?.isValid == true) {
                    Resource.Success(true)
                } else {
                    Resource.Error("Token inválido o expirado")
                }
            }
            is Resource.Error -> Resource.Error(result.message ?: "Error al validar token")
            is Resource.Loading -> Resource.Loading()
        }
    }

    override suspend fun logout(token: String): Resource<Unit> {
        return remoteDataSource.logout(token)
    }
}

