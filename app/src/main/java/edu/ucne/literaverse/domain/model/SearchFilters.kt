package edu.ucne.literaverse.domain.model

data class SearchFilters(
    val query: String = "",
    val genero: String? = null,
    val categoria: String? = null,
    val estado: EstadoNovel? = null,
    val ordenarPor: OrdenCriterio = OrdenCriterio.RELEVANCIA
)

enum class EstadoNovel(val value: String, val displayName: String) {
    EN_PROGRESO("En progreso", "En progreso"),
    COMPLETA("Completa", "Completa"),
    PAUSADA("Pausada", "Pausada")
}

enum class OrdenCriterio(val value: String, val displayName: String) {
    RELEVANCIA("relevancia", "Relevancia"),
    POPULARIDAD("popularidad", "Más populares"),
    MAS_RECIENTE("fecha", "Más recientes"),
    MAS_LEIDAS("vistas", "Más leídas"),
    ALFABETICO("titulo", "A-Z")
}