package edu.ucne.literaverse.data.mappers

import edu.ucne.literaverse.data.remote.dto.NovelDto
import edu.ucne.literaverse.domain.model.Novel

fun NovelDto.toDomain(): Novel {
    return Novel(
        id = novelaId,
        titulo = titulo,
        descripcion = descripcion ?: "",
        portada = portada,
        autor = autor,
        autorId = autorId,
        genero = genero,
        categoria = categoria,
        estado = estado ?: "Desconocido",
        vistas = vistas ?: 0,
        likes = likes ?: 0,
        capitulos = capitulos ?: 0,
        fechaPublicacion = fechaPublicacion,
        fechaActualizacion = fechaActualizacion,
        isPending = false
    )
}

fun List<NovelDto>.toDomain(): List<Novel> {
    return map { it.toDomain() }
}