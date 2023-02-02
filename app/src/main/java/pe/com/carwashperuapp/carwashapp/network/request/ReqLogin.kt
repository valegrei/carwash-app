package pe.com.carwashperuapp.carwashapp.network.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class ReqLogin(
    @Json(name = "correo") var correo: String,
    @Json(name = "clave") var clave: String
)