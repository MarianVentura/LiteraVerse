package edu.ucne.literaverse.domain.usecase.storyUseCases

import edu.ucne.literaverse.data.remote.Resource
import edu.ucne.literaverse.domain.model.StoryDetail
import edu.ucne.literaverse.domain.repository.StoryRepository
import javax.inject.Inject

class CreateStoryUseCase @Inject constructor(
    private val repository: StoryRepository
) {
    suspend operator fun invoke(
        userId: Int,
        title: String,
        synopsis: String,
        genre: String,
        tags: String?
    ): Resource<StoryDetail> {
        if (title.isBlank()) {
            return Resource.Error("El título es obligatorio")
        }
        if (title.length > 200) {
            return Resource.Error("El título no puede exceder 200 caracteres")
        }
        if (synopsis.isBlank()) {
            return Resource.Error("La sinopsis es obligatoria")
        }
        if (synopsis.length > 1000) {
            return Resource.Error("La sinopsis no puede exceder 1000 caracteres")
        }
        if (genre.isBlank()) {
            return Resource.Error("El género es obligatorio")
        }

        return repository.createStory(userId, title, synopsis, genre, tags)
    }
}