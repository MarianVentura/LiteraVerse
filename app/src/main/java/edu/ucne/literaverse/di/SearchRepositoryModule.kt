package edu.ucne.literaverse.di


import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import edu.ucne.literaverse.data.repository.SearchRepositoryImpl
import edu.ucne.literaverse.domain.repository.SearchRepository
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
interface SearchRepositoryModule {


    @Binds
    @Singleton
     fun bindSearchRepository(
        searchRepositoryImpl: SearchRepositoryImpl
    ): SearchRepository
}


