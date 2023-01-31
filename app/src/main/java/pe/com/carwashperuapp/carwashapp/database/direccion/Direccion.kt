package pe.com.carwashperuapp.carwashapp.database.direccion

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json

enum class TipoDireccion(val id: Int, val nombre: String) {
    LOCAL(1, "Local"),
    CASA(2, "Casa"),
    OFICINA(3, "Oficina"),
    OTRO(4, "Otro")
}

@Entity(tableName = "direccion")
data class Direccion(
    @PrimaryKey
    @ColumnInfo(name = "id")
    @Json(name = "id")
    val id: Int,
    @ColumnInfo(name = "departamento")
    @Json(name = "departamento")
    val departamento: String,
    @ColumnInfo(name = "provincia")
    @Json(name = "provincia")
    val provincia: String,
    @ColumnInfo(name = "distrito")
    @Json(name = "distrito")
    val distrito: String,
    @ColumnInfo(name = "ubigeo")
    @Json(name = "ubigeo")
    val ubigeo: String,
    @ColumnInfo(name = "direccion")
    @Json(name = "direccion")
    val direccion: String,
    @ColumnInfo(name = "latitud")
    @Json(name = "latitud")
    val latitud: String,
    @ColumnInfo(name = "longitud")
    @Json(name = "longitud")
    val longitud: String,
    @ColumnInfo(name = "estado")
    @Json(name = "estado")
    val estado: Boolean,
    @ColumnInfo(name = "idUsuario")
    @Json(name = "idUsuario")
    val idUsuario: Int,
    @ColumnInfo(name = "tipo")
    @Json(name = "tipo")
    val tipo: Int?,
) {
    fun getUbigeoDireccion(): String = if (tipo == TipoDireccion.LOCAL.id) {
        "$departamento - $provincia - $distrito\n$direccion"
    } else {
        direccion
    }

    override fun toString(): String = direccion
    fun nombreTipo(): String = when (tipo) {
        TipoDireccion.LOCAL.id -> TipoDireccion.LOCAL.nombre
        TipoDireccion.CASA.id -> TipoDireccion.CASA.nombre
        TipoDireccion.OFICINA.id -> TipoDireccion.OFICINA.nombre
        TipoDireccion.OTRO.id -> TipoDireccion.OTRO.nombre
        else -> ""
    }
}