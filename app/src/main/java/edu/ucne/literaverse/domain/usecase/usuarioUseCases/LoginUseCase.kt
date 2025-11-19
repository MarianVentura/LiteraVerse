package edu.ucne.literaverse.domain.usecase.usuarioUseCases

import edu.ucne.literaverse.data.remote.Resource
import edu.ucne.literaverse.domain.model.Usuario
import edu.ucne.literaverse.domain.repository.UsuarioRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: UsuarioRepository
) {
    suspend operator fun invoke(userName: String, password: String): Resource<Usuario> {
        if (userName.isBlank()) {
            return Resource.Error("El nombre de usuario es obligatorio")
        }
        if (password.isBlank()) {
            return Resource.Error("La contraseña es obligatoria")
        }

        return repository.login(userName, password)
    }
}