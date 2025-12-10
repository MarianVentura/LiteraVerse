package edu.ucne.literaverse.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import edu.ucne.literaverse.data.repository.ChapterRepositoryImpl
import edu.ucne.literaverse.domain.repository.ChapterRepository


@Module
@InstallIn(SingletonComponent::class)
interface ChapterRepositoryModule {

    @Binds
    fun bindChapterRepository(
        impl: ChapterRepositoryImpl
    ): ChapterRepository
}