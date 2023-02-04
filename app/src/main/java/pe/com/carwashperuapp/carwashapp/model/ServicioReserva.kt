package pe.com.carwashperuapp.carwashapp.model

import android.icu.math.BigDecimal
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import pe.com.carwashperuapp.carwashapp.ui.util.formatoPrecio

@JsonClass(generateAdapter = true)
class ServicioReserva(
    @Json(name = "id") val id: Int,
    @Json(name = "nombre") val nombre: String? = null,
    @Json(name = "precio") val precio: BigDecimal? = null,
    @Json(name = "duracion") val duracion: Int? = null,
    @Json(name = "ServicioDetalle") val detalle: ServicioReservaDetalle?=null,
    var seleccionado: Boolean? = false,
) {
    fun getNombrePrecio(): String = "$nombre\nS/ ${getPrecioFormateado()}"
    fun getPrecioFormateado(): String = formatoPrecio(precio)
    fun getDuracionFormateado(): String = "$duracion min"
}

@JsonClass(generateAdapter = true)
class ServicioReservaDetalle(
    @Json(name = "id") val id: Int,
    @Json(name = "precio") val precio: BigDecimal,
    @Json(name = "duracion") val duracion: Int,
    @Json(name = "atendido") var atendido: Boolean,
) {
    fun getPrecioFormateado(): String = formatoPrecio(precio)
    fun getDuracionFormateado(): String = "$duracion min"
}