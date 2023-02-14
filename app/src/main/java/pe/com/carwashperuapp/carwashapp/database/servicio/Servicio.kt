package pe.com.carwashperuapp.carwashapp.database.servicio

import android.icu.math.BigDecimal
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import pe.com.carwashperuapp.carwashapp.ui.util.formatoPrecio

enum class Duracion(val duracion: Int, val nombre: String){
    MIN_15(15,"15m"),
    MIN_30(30,"30m"),
    MIN_45(45,"45m"),
    MIN_60(60,"1h"),
    MIN_90(90,"1h 30m"),
    MIN_120(120,"2h"),
    MIN_150(150,"2h 30m"),
    MIN_180(180,"3h");

    override fun toString(): String {
        return nombre
    }
}

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
    fun getNombrePrecio(): String = "$nombre\nS/ ${getPrecioFormateado()} - ${getDuracionEnum().nombre}"
    fun getNombreFormateado(): String = "Nombre: $nombre"
    fun getPrecioLabel(): String = "Precio: S/ ${getPrecioFormateado()}"
    fun getPrecioFormateado(): String = formatoPrecio(precio)
    fun getDuracionFormateado(): String = "Duración: $duracion minutos"

    fun getDuracionEnum(): Duracion{
        return when(duracion){
            Duracion.MIN_15.duracion -> Duracion.MIN_15
            Duracion.MIN_30.duracion -> Duracion.MIN_30
            Duracion.MIN_45.duracion -> Duracion.MIN_45
            Duracion.MIN_60.duracion -> Duracion.MIN_60
            Duracion.MIN_90.duracion -> Duracion.MIN_90
            Duracion.MIN_120.duracion -> Duracion.MIN_120
            Duracion.MIN_150.duracion -> Duracion.MIN_150
            Duracion.MIN_180.duracion -> Duracion.MIN_180
            else -> Duracion.MIN_180
        }
    }
}