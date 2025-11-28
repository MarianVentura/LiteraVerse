package edu.ucne.literaverse.data.mappers

import edu.ucne.literaverse.data.local.entities.UsuarioEntity
import edu.ucne.literaverse.data.remote.dto.LoginRequest
import edu.ucne.literaverse.data.remote.dto.LoginResponse
import edu.ucne.literaverse.data.remote.dto.RegisterRequest
import edu.ucne.literaverse.domain.model.Usuario

fun Usuario.toLoginRequest(): LoginRequest {
    return LoginRequest(
        userName = userName,
        password = password
    )
}

fun Usuario.toRegisterRequest(): RegisterRequest {
    return RegisterRequest(
        userName = userName,
        password = password
    )
}

fun LoginResponse.toDomain(): Usuario {
    return Usuario(
        usuarioId = userId,
        userName = userName,
        password = "",
        token = token
    )
}

fun Usuario.toEntity(): UsuarioEntity {
    return UsuarioEntity(
        usuarioId = usuarioId,
        userName = userName,
        password = password
    )
}

fun UsuarioEntity.toDomain(): Usuario {
    return Usuario(
        usuarioId = usuarioId,
        userName = userName,
        password = password
    )
}