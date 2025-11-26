package edu.ucne.literaverse.domain.usecase.usuarioUseCases

import edu.ucne.literaverse.data.remote.Resource
import edu.ucne.literaverse.domain.model.Usuario
import edu.ucne.literaverse.domain.repository.UsuarioRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val repository: UsuarioRepository
) {
    suspend operator fun invoke(userName: String, password: String, confirmPassword: String): Resource<Usuario> {
        if (userName.isBlank()) {
            return Resource.Error("El nombre de usuario es obligatorio")
        }
        if (password.isBlank()) {
            return Resource.Error("La contraseña es obligatoria")
        }
        if (confirmPassword.isBlank()) {
            return Resource.Error("Debes confirmar la contraseña")
        }
        if (password != confirmPassword) {
            return Resource.Error("Las contraseñas no coinciden")
        }
        if (password.length < 6) {
            return Resource.Error("La contraseña debe tener al menos 6 caracteres")
        }

        return repository.register(userName, password)
    }
}