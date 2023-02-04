package pe.com.carwashperuapp.carwashapp.model

import android.icu.math.BigDecimal
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import pe.com.carwashperuapp.carwashapp.ui.util.formatoPrecio

enum class ServicioEstado(val id: Int, val nombre: String) {
    NO_ATENDIDO(1, "No atendido"),
    ATENDIDO(2, "Atendido"),
    ANULADO(3, "Anulado")
}

@JsonClass(generateAdapter = true)
class ServicioReserva(
    @Json(name = "id") val id: Int,
    @Json(name = "nombre") val nombre: String? = null,
    @Json(name = "precio") val precio: BigDecimal? = null,
    @Json(name = "duracion") val duracion: Int? = null,
    @Json(name = "ReservaServicios") val detalle: ServicioReservaDetalle? = null,
    var seleccionado: Boolean? = false,
) {
    fun getNombrePrecio(): String = "$nombre\nS/ ${getPrecioFormateado()} - ${getDuracionFormateado()}"
    fun getNombrePrecio2(): String =
        "$nombre\nS/ ${detalle?.getPrecioFormateado()} - ${detalle?.getDuracionFormateado()}"

    fun getPrecioFormateado(): String = formatoPrecio(precio)
    fun getDuracionFormateado(): String = "$duracion min"
}

@JsonClass(generateAdapter = true)
class ServicioReservaDetalle(
    @Json(name = "precio") val precio: BigDecimal,
    @Json(name = "duracion") val duracion: Int,
    @Json(name = "estado") var estado: Int,
) {
    fun getPrecioFormateado(): String = formatoPrecio(precio)
    fun getDuracionFormateado(): String = "$duracion min"
    fun getEstadoNombre(): String = when (estado) {
        ServicioEstado.NO_ATENDIDO.id -> ServicioEstado.NO_ATENDIDO.nombre
        ServicioEstado.ATENDIDO.id -> ServicioEstado.ATENDIDO.nombre
        ServicioEstado.ANULADO.id -> ServicioEstado.ANULADO.nombre
        else -> ""
    }

}