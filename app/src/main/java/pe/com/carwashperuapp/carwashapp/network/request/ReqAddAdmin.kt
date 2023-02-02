package pe.com.carwashperuapp.carwashapp.network.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class ReqAddAdmin(
    @Json(name = "correo") var correo: String,
    @Json(name = "nombres") var nombres: String,
    @Json(name = "apellidoPaterno") var apellidoPaterno: String,
    @Json(name = "apellidoMaterno") var apellidoMaterno: String,
)