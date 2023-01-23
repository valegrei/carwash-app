package pe.com.carwashperuapp.carwashapp.network.request

import com.squareup.moshi.Json

class ReqCambiarClaveAdmin(
    @Json(name = "claveNueva") val claveNueva: String,
)