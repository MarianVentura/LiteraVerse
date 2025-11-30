package edu.ucne.literaverse.domain.usecase.usuarioUseCases

import edu.ucne.literaverse.data.remote.Resource
import edu.ucne.literaverse.domain.repository.UsuarioRepository
import javax.inject.Inject

class ValidateTokenUseCase @Inject constructor(
    private val usuarioRepository: UsuarioRepository
) {
    suspend operator fun invoke(token: String): Resource<Boolean> {
        return usuarioRepository.validateToken(token)
    }
}