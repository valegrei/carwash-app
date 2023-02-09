package pe.com.carwashperuapp.carwashapp.network.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import pe.com.carwashperuapp.carwashapp.model.ServicioReserva

@JsonClass(generateAdapter = true)
class ReqReservar(
    @Json(name = "idHorarios") val idHorarios: List<Int>,
    @Json(name = "idCliente") val idCliente: Int,
    @Json(name = "idVehiculo") val idVehiculo: Int,
    @Json(name = "idDistrib") val idDistrib: Int,
    @Json(name = "idLocal") val idLocal: Int,
    @Json(name = "fecha") val fecha: String,
    @Json(name = "horaIni") val horaIni: String,
    @Json(name = "fechaHora") val fechaHora: String,
    @Json(name = "duracionTotal") val duracionTotal: Int,
    @Json(name = "servicios") val servicios: List<ServicioReserva>,
)