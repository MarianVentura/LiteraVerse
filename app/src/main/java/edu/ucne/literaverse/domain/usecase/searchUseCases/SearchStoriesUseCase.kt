package edu.ucne.literaverse.domain.usecase.searchUseCases


import edu.ucne.literaverse.data.remote.Resource
import edu.ucne.literaverse.domain.model.SearchFilters
import edu.ucne.literaverse.domain.model.Story
import edu.ucne.literaverse.domain.repository.SearchRepository
import javax.inject.Inject


class SearchStoriesUseCase @Inject constructor(
    private val repository: SearchRepository
) {
    suspend operator fun invoke(filters: SearchFilters): Resource<List<Story>> {
        return repository.searchStories(filters)
    }
}


