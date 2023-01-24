package pe.com.carwashperuapp.carwashapp.model

import android.icu.math.BigDecimal
import android.icu.text.DecimalFormat
import com.squareup.moshi.Json

class ServicioReserva(
    @Json(name = "id") val id: Int,
    @Json(name = "nombre") val nombre: String,
    @Json(name = "precio") val precio: BigDecimal,
    var seleccionado: Boolean? = false,
) {
    fun getNombrePrecio(): String = "$nombre\nS/ ${getPrecioFormateado()}"
    fun getNombreFormateado(): String = "Nombre: $nombre"
    fun getPrecioLabel(): String = "Precio: S/ ${getPrecioFormateado()}"
    fun getPrecioFormateado(): String = DecimalFormat("#,###.00").format(precio)
}