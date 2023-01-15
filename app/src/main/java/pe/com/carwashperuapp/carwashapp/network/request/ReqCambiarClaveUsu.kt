package pe.com.carwashperuapp.carwashapp.network.request

import com.squareup.moshi.Json

class ReqCambiarClaveUsu(
    @Json(name = "claveAnterior") val claveAnterior: String,
    @Json(name = "claveNueva") val claveNueva: String,
)