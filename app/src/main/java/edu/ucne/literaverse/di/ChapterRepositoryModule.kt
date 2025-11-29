package edu.ucne.literaverse.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import edu.ucne.literaverse.data.repository.ChapterRepositoryImpl
import edu.ucne.literaverse.domain.repository.ChapterRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ChapterRepositoryModule {
    @Binds
    @Singleton
    abstract fun bindChapterRepository(
        chapterRepositoryImpl: ChapterRepositoryImpl
    ): ChapterRepository
}