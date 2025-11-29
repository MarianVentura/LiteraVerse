package edu.ucne.literaverse.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import edu.ucne.literaverse.data.local.dao.UsuarioDao
import edu.ucne.literaverse.data.local.dao.ChapterDao
import edu.ucne.literaverse.data.local.dao.StoryDao
import edu.ucne.literaverse.data.local.entities.UsuarioEntity
import edu.ucne.literaverse.data.local.entities.ChapterEntity
import edu.ucne.literaverse.data.local.entities.StoryEntity

@Database(
    entities = [
        UsuarioEntity::class,
        StoryEntity::class,
        ChapterEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDao
    abstract fun storyDao(): StoryDao
    abstract fun chapterDao(): ChapterDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "literaverse_database"
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}