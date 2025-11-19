package edu.ucne.literaverse.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Usuarios")
data class UsuarioEntity(
    @PrimaryKey(autoGenerate = true)
    val usuarioId: Int = 0,
    val userName: String,
    val password: String
)