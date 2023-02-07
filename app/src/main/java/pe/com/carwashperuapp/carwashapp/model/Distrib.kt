package pe.com.carwashperuapp.carwashapp.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class Distrib(
    @Json(name = "id") val id: Int,
    @Json(name = "razonSocial") val razonSocial: String,
    @Json(name = "nroDocumento") val nroDocumento: String?,
    @Json(name = "nroCel1") val nroCel1: String?,
    @Json(name = "nroCel2") val nroCel2: String?,
    @Json(name = "idTipoDocumento") val idTipoDocumento: Int?,
    @Json(name = "acercaDe") val acercaDe: String?,
    @Json(name = "path") val path: String?,
    @Json(name = "Servicios") val servicios: List<ServicioReserva>? = null,
)