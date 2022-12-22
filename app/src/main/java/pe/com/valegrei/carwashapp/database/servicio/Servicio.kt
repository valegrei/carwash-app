package pe.com.valegrei.carwashapp.database.servicio

import android.icu.math.BigDecimal
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json

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
    @Json(name = "estado")
    @ColumnInfo(name = "estado")
    var estado: Boolean,
    @Json(name = "idDistrib")
    @ColumnInfo(name = "idDistrib")
    var idDistrib: Int,
){
    fun getNombrePrecio(): String{
        return "$nombre\nS/ $precio"
    }
    fun getNombreFormateado(): String{
        return "Nombre: $nombre"
    }
    fun getPrecioFormateado(): String{
        return "Precio: S/ $precio"
    }
}