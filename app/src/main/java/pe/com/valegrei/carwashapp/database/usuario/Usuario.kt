package pe.com.valegrei.carwashapp.database.usuario

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.squareup.moshi.Json

@Entity(tableName = "usuario")
data class Usuario(
    @PrimaryKey
    @Json(name = "id")
    @ColumnInfo(name = "id")
    var id: Int?,
    @Json(name = "correo")
    @ColumnInfo(name = "id")
    var correo: String,
    @Json(name = "clave")
    @Ignore
    var clave: String?,
    @Json(name = "nombres")
    @ColumnInfo(name = "id")
    var nombres: String?,
    @Json(name = "apellidoPaterno")
    @ColumnInfo(name = "id")
    var apellidoPaterno: String?,
    @Json(name = "apellidoMaterno")
    @ColumnInfo(name = "id")
    var apellidoMaterno: String?,
    @Json(name = "razonSocial")
    @ColumnInfo(name = "id")
    var razonSocial: String?,
    @Json(name = "nroDocumento")
    @ColumnInfo(name = "id")
    var nroDocumento: String?,
    @Json(name = "nroCel1")
    @ColumnInfo(name = "id")
    var nroCel1: String?,
    @Json(name = "nroCel2")
    @ColumnInfo(name = "id")
    var nroCel2: String?,
    @Json(name = "distAct")
    @ColumnInfo(name = "id")
    var distAct: Boolean,
    @Json(name = "verificado")
    @ColumnInfo(name = "id")
    var verificado: Boolean,
    @Json(name = "estado")
    @ColumnInfo(name = "id")
    var estado: Boolean,
    @Json(name = "idTipoUsuario")
    @ColumnInfo(name = "id")
    var idTipoUsuario: Int,
    @Json(name = "idTipoDocumento")
    @ColumnInfo(name = "id")
    var idTipoDocumento: Int,
    @Json(name = "createdAt")
    @ColumnInfo(name = "id")
    var createdAt: String?,
    @Json(name = "updatedAt")
    @ColumnInfo(name = "id")
    var updatedAt: String?,
)