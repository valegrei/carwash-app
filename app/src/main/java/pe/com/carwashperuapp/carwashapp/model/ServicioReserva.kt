package pe.com.carwashperuapp.carwashapp.model

import android.icu.math.BigDecimal
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import pe.com.carwashperuapp.carwashapp.ui.util.formatoPrecio

@JsonClass(generateAdapter = true)
class ServicioReserva(
    @Json(name = "id") val id: Int,
    @Json(name = "nombre") val nombre: String,
    @Json(name = "precio") val precio: BigDecimal,
    var seleccionado: Boolean? = false,
) {
    fun getNombrePrecio(): String = "$nombre\nS/ ${getPrecioFormateado()}"
    fun getNombreFormateado(): String = "Nombre: $nombre"
    fun getPrecioLabel(): String = "Precio: S/ ${getPrecioFormateado()}"
    fun getPrecioFormateado(): String = formatoPrecio(precio)
    fun cambiarSeleccion() {
        seleccionado != seleccionado
    }
}