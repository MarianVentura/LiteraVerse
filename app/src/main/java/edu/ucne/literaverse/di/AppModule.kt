package edu.ucne.literaverse.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import edu.ucne.literaverse.data.local.database.AppDatabase
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AppModule {
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context) =
        Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "LiteraVerse.db"
        ).fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideUsuarioDao(appDatabase: AppDatabase) = appDatabase.usuarioDao()

    @Provides
    fun provideStoryDao(appDatabase: AppDatabase) = appDatabase.storyDao()

    @Provides
    fun provideChapterDao(appDatabase: AppDatabase) = appDatabase.chapterDao()
}