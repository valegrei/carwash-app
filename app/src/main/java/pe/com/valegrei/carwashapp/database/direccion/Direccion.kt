package pe.com.valegrei.carwashapp.database.direccion

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json

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
){

    fun getUbigeoDireccion(): String{
        return "$departamento - $provincia - $distrito\n$direccion"
    }
}