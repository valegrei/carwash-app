package pe.com.carwashperuapp.carwashapp.network.request

import com.squareup.moshi.Json
import pe.com.carwashperuapp.carwashapp.model.ServicioReserva

class ReqReservar(
    @Json(name = "idHorario") val idHorario: Int,
    @Json(name = "idCliente") val idCliente: Int,
    @Json(name = "idVehiculo") val idVehiculo: Int,
    @Json(name = "servicios") val servicios: List<ServicioReserva>,
)