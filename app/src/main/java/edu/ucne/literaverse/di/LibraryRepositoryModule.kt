package edu.ucne.literaverse.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import edu.ucne.literaverse.data.repository.LibraryRepositoryImpl
import edu.ucne.literaverse.domain.repository.LibraryRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface LibraryRepositoryModule {
    @Binds
    @Singleton
     fun bindLibraryRepository(
        libraryRepositoryImpl: LibraryRepositoryImpl
    ): LibraryRepository
}