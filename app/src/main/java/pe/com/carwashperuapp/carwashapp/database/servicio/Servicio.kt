package pe.com.carwashperuapp.carwashapp.database.servicio

import android.icu.math.BigDecimal
import android.icu.text.DecimalFormat
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
        return "$nombre\nS/ ${getPrecioFormateado()}"
    }
    fun getNombreFormateado(): String{
        return "Nombre: $nombre"
    }
    fun getPrecioLabel(): String{
        return "Precio: S/ ${getPrecioFormateado()}"
    }

    fun getPrecioFormateado(): String{
        val df = DecimalFormat("#,###.00")
        return df.format(precio)
    }
}