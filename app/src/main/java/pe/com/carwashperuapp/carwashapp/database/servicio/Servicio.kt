package pe.com.carwashperuapp.carwashapp.database.servicio

import android.icu.math.BigDecimal
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import pe.com.carwashperuapp.carwashapp.ui.util.formatoPrecio

@JsonClass(generateAdapter = true)
@Entity(tableName = "servicios")
data class Servicio(
    @PrimaryKey
    @Json(name = "id")
    @ColumnInfo(name = "id")
    var id: Int,
    @Json(name = "nombre")
    @ColumnInfo(name = "nombre")
    var nombre: String,
    @Json(name = "precio")
    @ColumnInfo(name = "precio")
    var precio: BigDecimal,
    @Json(name = "duracion")
    @ColumnInfo(name = "duracion")
    var duracion: Int,
    @Json(name = "estado")
    @ColumnInfo(name = "estado")
    var estado: Boolean,
    @Json(name = "idDistrib")
    @ColumnInfo(name = "idDistrib")
    var idDistrib: Int,
) {
    fun getNombrePrecio(): String = "$nombre\nS/ ${getPrecioFormateado()} - $duracion min"
    fun getNombreFormateado(): String = "Nombre: $nombre"
    fun getPrecioLabel(): String = "Precio: S/ ${getPrecioFormateado()}"
    fun getPrecioFormateado(): String = formatoPrecio(precio)
    fun getDuracionFormateado(): String = "Duración: $duracion minutos"
}