package pe.com.carwashperuapp.carwashapp.model

import android.icu.math.BigDecimal
import com.squareup.moshi.Json
import pe.com.carwashperuapp.carwashapp.database.vehiculo.Vehiculo

class Reserva(
    @Json(name = "id") val id: Int,
    @Json(name = "Horario") val horario: Horario? = null,
    @Json(name = "Servicios") val servicioReserva: List<ServicioReserva>? = null,
    @Json(name = "Vehiculo") val vehiculo: Vehiculo? = null,
) {
    fun calcularTotalServicios(): BigDecimal {
        var total = BigDecimal.ZERO
        servicioReserva?.forEach {
            total = total.add(it.precio)
        }
        return total
    }
}