package pe.com.carwashperuapp.carwashapp.model

import com.squareup.moshi.Json

class Distrib(
    @Json(name = "id") val id: Int,
    @Json(name = "razonSocial") val razonSocial: String,
    @Json(name = "nroDocumento") val nroDocumento: String?,
    @Json(name = "nroCel1") val nroCel1: String?,
    @Json(name = "nroCel2") val nroCel2: String?,
    @Json(name = "idTipoDocumento") val idTipoDocumento: Int?,
    @Json(name = "Servicios") val servicios: List<ServicioReserva>? = null,
)