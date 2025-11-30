package edu.ucne.literaverse.domain.model

data class Novel(
    val id: Int,
    val titulo: String,
    val descripcion: String,
    val portada: String?,
    val autor: String,
    val autorId: Int?,
    val genero: String?,
    val categoria: String?,
    val estado: String, // "En progreso", "Completa", "Pausada"
    val vistas: Int,
    val likes: Int,
    val capitulos: Int,
    val fechaPublicacion: String?,
    val fechaActualizacion: String?,
    val isPending: Boolean = false
)