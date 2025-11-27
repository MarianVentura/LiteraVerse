package edu.ucne.literaverse.data.mappers

import edu.ucne.literaverse.data.remote.dto.StoryResponse
import edu.ucne.literaverse.domain.model.Story

fun StoryResponse.toDomain(): Story = Story(
    storyId = storyId,
    title = title,
    author = author,
    description = description,
    coverImageUrl = coverImageUrl,
    genres = genres,
    reads = reads,
    chapters = chapters,
    status = status
)