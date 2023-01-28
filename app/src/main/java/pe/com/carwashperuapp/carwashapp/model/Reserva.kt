package pe.com.carwashperuapp.carwashapp.model

import com.squareup.moshi.Json

class Reserva(
    @Json(name = "id") val id: Int,
    @Json(name = "idCliente") val idCliente: Int,
    @Json(name = "idVehiculo") val idVehiculo: Int,
    @Json(name = "estado") val estado: Boolean,
    @Json(name = "Horario") val horario: Horario? = null,
    @Json(name = "Servicio") val servicioReserva: ServicioReserva? = null,
) {
}