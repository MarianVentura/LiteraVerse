package edu.ucne.literaverse.data.mappers

import edu.ucne.literaverse.data.remote.dto.GenreResponse
import edu.ucne.literaverse.domain.model.Genre

fun GenreResponse.toDomain(): Genre = Genre(
    genreId = genreId,
    name = name
)