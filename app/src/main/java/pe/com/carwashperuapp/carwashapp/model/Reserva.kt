package pe.com.carwashperuapp.carwashapp.model

import android.icu.math.BigDecimal
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import pe.com.carwashperuapp.carwashapp.database.vehiculo.Vehiculo
import pe.com.carwashperuapp.carwashapp.ui.util.formatHora
import pe.com.carwashperuapp.carwashapp.ui.util.formatHoraFin
import pe.com.carwashperuapp.carwashapp.ui.util.formatoFechaDBaHum

enum class ReservaEstado(val id: Int, val nombre: String) {
    NO_ATENDIDO(1, "No atendido"),
    ATENDIDO(2, "Atendido"),
    ATENDIENDO(3, "Atendiendo"),
    ANULADO(0, "Anulado"),
}

@JsonClass(generateAdapter = true)
class Reserva(
    @Json(name = "id") val id: Int,
    @Json(name = "fecha") val fecha: String,
    @Json(name = "horaIni") val horaIni: String,
    @Json(name = "duracionTotal") val duracionTotal: Int,
    @Json(name = "estadoAtencion") val estadoAtencion: Int? = null,
    @Json(name = "Servicios") val servicioReserva: List<ServicioReserva>? = null,
    @Json(name = "Vehiculo") val vehiculo: Vehiculo? = null,
    @Json(name = "cliente") val cliente: Cliente? = null,
    @Json(name = "distrib") val distrib: Distrib? = null,
    @Json(name = "Local") val local: Local? = null,
) {
    fun calcularTotalServicios(): BigDecimal {
        var total = BigDecimal.ZERO
        servicioReserva?.forEach {
            total = total.add(it.detalle?.precio)
        }
        return total
    }

    override fun toString(): String = formatHora(horaIni)
    fun fechaHoraIni(): String = "${formatoFechaDBaHum(fecha)}, ${formatHora(horaIni)} - ${
        formatHoraFin(horaIni, duracionTotal)
    }"

    fun fechaHoraIni2(): String = "${formatoFechaDBaHum(fecha)}\n${formatHora(horaIni)} - ${
        formatHoraFin(horaIni, duracionTotal)
    }"

    fun fechaHoraDB(): String = "$fecha $horaIni"
    fun getEstadoNombre(): String = when (estadoAtencion) {
        ReservaEstado.NO_ATENDIDO.id -> ReservaEstado.NO_ATENDIDO.nombre
        ReservaEstado.ATENDIDO.id -> ReservaEstado.ATENDIDO.nombre
        ReservaEstado.ATENDIENDO.id -> ReservaEstado.ATENDIENDO.nombre
        ReservaEstado.ANULADO.id -> ReservaEstado.ANULADO.nombre
        else -> ""
    }
}