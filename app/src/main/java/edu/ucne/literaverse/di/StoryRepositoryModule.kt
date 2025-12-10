package edu.ucne.literaverse.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import edu.ucne.literaverse.data.repository.StoryRepositoryImpl
import edu.ucne.literaverse.domain.repository.StoryRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface StoryRepositoryModule {
    @Binds
    @Singleton
      fun bindStoryRepository(
        storyRepositoryImpl: StoryRepositoryImpl
    ): StoryRepository
}