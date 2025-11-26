package edu.ucne.literaverse.data.repository

import edu.ucne.literaverse.data.local.dao.UsuarioDao
import edu.ucne.literaverse.data.mappers.toDomain
import edu.ucne.literaverse.data.mappers.toRequest
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

        val request = usuario.toRequest()

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
        val usuariosResult = remoteDataSource.getUsuarios()

        when (usuariosResult) {
            is Resource.Success -> {
                val usuarios = usuariosResult.data ?: emptyList()
                val usuarioExistente = usuarios.find {
                    it.userName.equals(userName, ignoreCase = true)
                }

                if (usuarioExistente != null) {
                    return Resource.Error("Este nombre de usuario ya está en uso")
                }
            }
            is Resource.Error -> {
                return Resource.Error("Error al verificar disponibilidad del usuario")
            }
            is Resource.Loading -> {}
        }

        val usuario = Usuario(
            usuarioId = 0,
            userName = userName,
            password = password
        )

        val request = usuario.toRequest()

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
        return when (val result = remoteDataSource.getUsuarios()) {
            is Resource.Success -> {
                val usuarios = result.data?.map { it.toDomain() } ?: emptyList()
                Resource.Success(usuarios)
            }
            is Resource.Error -> Resource.Error(result.message ?: "Error al obtener usuarios")
            is Resource.Loading -> Resource.Loading()
        }
    }
}