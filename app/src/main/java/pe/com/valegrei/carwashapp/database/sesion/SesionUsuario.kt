package pe.com.valegrei.carwashapp.database.sesion

import androidx.room.Embedded
import androidx.room.Relation
import pe.com.valegrei.carwashapp.database.usuario.Usuario

data class SesionUsuario(
    @Embedded val sesion: Sesion,
    @Relation(
        parentColumn = "id_usuario",
        entityColumn = "id"
    )
    val usuario: Usuario
)