package edu.ucne.literaverse.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import edu.ucne.literaverse.data.repository.UsuarioRepositoryImpl
import edu.ucne.literaverse.domain.repository.UsuarioRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {
    @Binds
    @Singleton
    fun bindUsuarioRepository(
        usuarioRepositoryImpl: UsuarioRepositoryImpl
    ): UsuarioRepository
}