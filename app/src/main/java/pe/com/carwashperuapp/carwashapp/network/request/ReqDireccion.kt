package pe.com.carwashperuapp.carwashapp.network.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class ReqDireccion(
    @Json(name = "departamento") var departamento: String?,
    @Json(name = "provincia") var provincia: String?,
    @Json(name = "distrito") var distrito: String?,
    @Json(name = "ubigeo") var ubigeo: String?,
    @Json(name = "direccion") var direccion: String,
    @Json(name = "latitud") var latitud: String,
    @Json(name = "longitud") var longitud: String,
    @Json(name = "tipo") var tipo: Int,
)