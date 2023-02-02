package pe.com.carwashperuapp.carwashapp.network.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class ReqCambiarClaveAdmin(
    @Json(name = "claveNueva") val claveNueva: String,
)