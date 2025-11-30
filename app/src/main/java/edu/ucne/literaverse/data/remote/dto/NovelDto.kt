package edu.ucne.literaverse.data.remote.dto

data class NovelDto(
    val novelaId: Int,
    val titulo: String,
    val descripcion: String?,
    val portada: String?,
    val autor: String,
    val autorId: Int?,
    val genero: String?,
    val categoria: String?,
    val estado: String?,
    val vistas: Int?,
    val likes: Int?,
    val capitulos: Int?,
    val fechaPublicacion: String?,
    val fechaActualizacion: String?
)

data class SearchNovelsResponse(
    val data: List<NovelDto>?,
    val message: String?,
    val success: Boolean?
)