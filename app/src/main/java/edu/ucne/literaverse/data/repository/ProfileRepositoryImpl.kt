package edu.ucne.literaverse.data.repository

import edu.ucne.literaverse.data.mappers.toDomain
import edu.ucne.literaverse.data.mappers.toDomainList
import edu.ucne.literaverse.data.remote.RemoteDataSource
import edu.ucne.literaverse.data.remote.Resource
import edu.ucne.literaverse.domain.model.Session
import edu.ucne.literaverse.domain.model.UserProfile
import edu.ucne.literaverse.domain.repository.ProfileRepository
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource
) : ProfileRepository {

    override suspend fun getUserProfile(userId: Int): Resource<UserProfile> {
        return when (val result = remoteDataSource.getUserProfile(userId)) {
            is Resource.Success -> {
                result.data?.let {
                    Resource.Success(it.toDomain())
                } ?: Resource.Error("No se pudo obtener el perfil")
            }
            is Resource.Error -> Resource.Error(result.message ?: "Error al obtener el perfil")
            is Resource.Loading -> Resource.Loading()
        }
    }

    override suspend fun getUserSessions(userId: Int): Resource<List<Session>> {
        return when (val result = remoteDataSource.getUserSessions(userId)) {
            is Resource.Success -> {
                result.data?.let {
                    Resource.Success(it.toDomainList())
                } ?: Resource.Error("No se pudieron obtener las sesiones")
            }
            is Resource.Error -> Resource.Error(result.message ?: "Error al obtener las sesiones")
            is Resource.Loading -> Resource.Loading()
        }
    }

    override suspend fun logoutAllSessions(userId: Int): Resource<Unit> {
        return remoteDataSource.logoutAllSessions(userId)
    }
}