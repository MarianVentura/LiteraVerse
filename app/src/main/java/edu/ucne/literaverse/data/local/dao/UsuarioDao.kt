package edu.ucne.literaverse.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import edu.ucne.literaverse.data.local.entities.UsuarioEntity

@Dao
interface UsuarioDao {
    @Upsert
    suspend fun upsert(usuario: UsuarioEntity)

    @Query("SELECT * FROM Usuarios WHERE userName = :userName AND password = :password")
    suspend fun login(userName: String, password: String): UsuarioEntity?

    @Query("SELECT * FROM Usuarios WHERE usuarioId = :id")
    suspend fun getById(id: Int): UsuarioEntity?
}