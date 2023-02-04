package pe.com.carwashperuapp.carwashapp.model

import android.icu.math.BigDecimal
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import pe.com.carwashperuapp.carwashapp.database.vehiculo.Vehiculo

@JsonClass(generateAdapter = true)
class Reserva(
    @Json(name = "id") val id: Int,
    @Json(name = "Horario") val horario: Horario? = null,
    @Json(name = "Servicios") val servicioReserva: List<ServicioReserva>? = null,
    @Json(name = "Vehiculo") val vehiculo: Vehiculo? = null,
    @Json(name = "cliente") val cliente: Cliente? = null,
) {
    fun calcularTotalServicios(): BigDecimal {
        var total = BigDecimal.ZERO
        servicioReserva?.forEach {
            total = total.add(it.detalle?.precio)
        }
        return total
    }
}