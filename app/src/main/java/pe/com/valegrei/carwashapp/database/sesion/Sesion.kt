package pe.com.valegrei.carwashapp.database.sesion

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sesion")
data class Sesion(
    @PrimaryKey
    @ColumnInfo(name = "id_usuario")
    var idUsuario: Int,
    @ColumnInfo(name = "fecha_expira")
    var fechaExpira: String,
    @ColumnInfo(name = "token_jwt")
    var tokenAuth: String,
    @ColumnInfo(name = "estado")
    var estado: Boolean,
) {
}