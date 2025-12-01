package edu.ucne.literaverse.domain.model


data class SearchFilters(
    val query: String = "",
    val genre: String? = null,
    val status: String? = null,
    val sortBy: SortCriteria = SortCriteria.RELEVANCE
)


enum class SortCriteria(val value: String) {
    RELEVANCE("relevance"),
    POPULARITY("popularity"),
    RECENT("recent"),
    MOST_READ("most_read")
}




