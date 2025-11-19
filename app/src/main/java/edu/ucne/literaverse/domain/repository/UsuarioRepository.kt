package edu.ucne.literaverse.domain.repository

import edu.ucne.literaverse.data.remote.Resource
import edu.ucne.literaverse.domain.model.Usuario

interface UsuarioRepository {
    suspend fun login(userName: String, password: String): Resource<Usuario>
}

