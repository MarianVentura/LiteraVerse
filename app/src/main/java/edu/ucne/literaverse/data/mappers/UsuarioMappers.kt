package edu.ucne.literaverse.data.mappers

import edu.ucne.literaverse.data.local.entities.UsuarioEntity
import edu.ucne.literaverse.data.remote.dto.LoginRequest
import edu.ucne.literaverse.data.remote.dto.LoginResponse
import edu.ucne.literaverse.domain.model.Usuario

fun UsuarioEntity.toDomain(): Usuario = Usuario(
    usuarioId = usuarioId,
    userName = userName,
    password = password
)

fun Usuario.toEntity(): UsuarioEntity = UsuarioEntity(
    usuarioId = usuarioId,
    userName = userName,
    password = password
)

fun Usuario.toRequest(): LoginRequest = LoginRequest(
    userName = userName,
    password = password
)

fun LoginResponse.toDomain(): Usuario = Usuario(
    usuarioId = usuarioId,
    userName = userName,
    password = ""
)

