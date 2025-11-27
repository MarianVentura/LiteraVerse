package edu.ucne.literaverse.domain.usecase.homeUseCases

import edu.ucne.literaverse.data.remote.Resource
import edu.ucne.literaverse.domain.model.Genre
import edu.ucne.literaverse.domain.model.Story
import edu.ucne.literaverse.domain.repository.ExploreRepository
import javax.inject.Inject

class GetHomeDataUseCase @Inject constructor(
    private val repository: ExploreRepository
) {
    suspend fun getFeatured(): Resource<List<Story>> = repository.getFeaturedStories()
    suspend fun getPopular(): Resource<List<Story>> = repository.getPopularStories()
    suspend fun getNew(): Resource<List<Story>> = repository.getNewStories()
    suspend fun getGenres(): Resource<List<Genre>> = repository.getGenres()
    suspend fun getByGenre(genreName: String): Resource<List<Story>> = repository.getStoriesByGenre(genreName)
}