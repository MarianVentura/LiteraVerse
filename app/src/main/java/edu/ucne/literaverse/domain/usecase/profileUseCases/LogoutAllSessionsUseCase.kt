package edu.ucne.literaverse.domain.usecase.profileUseCases

import edu.ucne.literaverse.data.remote.Resource
import edu.ucne.literaverse.domain.repository.ProfileRepository
import javax.inject.Inject

class LogoutAllSessionsUseCase @Inject constructor(
    private val repository: ProfileRepository
) {
    suspend operator fun invoke(userId: Int): Resource<Unit> {
        if (userId <= 0) {
            return Resource.Error("ID de usuario inválido")
        }
        return repository.logoutAllSessions(userId)
    }
}