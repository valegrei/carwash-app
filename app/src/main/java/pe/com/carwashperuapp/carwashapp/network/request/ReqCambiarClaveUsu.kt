package pe.com.carwashperuapp.carwashapp.network.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class ReqCambiarClaveUsu(
    @Json(name = "claveAnterior") val claveAnterior: String,
    @Json(name = "claveNueva") val claveNueva: String,
)