package edu.ucne.literaverse.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import edu.ucne.literaverse.data.repository.ExploreRepositoryImpl
import edu.ucne.literaverse.domain.repository.ExploreRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface ExploreRepositoryModule {
    @Binds
    @Singleton
    fun bindExploreRepository(
        exploreRepositoryImpl: ExploreRepositoryImpl
    ): ExploreRepository
}