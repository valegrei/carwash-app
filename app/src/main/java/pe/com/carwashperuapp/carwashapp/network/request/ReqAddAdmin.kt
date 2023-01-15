package pe.com.carwashperuapp.carwashapp.network.request

import com.squareup.moshi.Json

class ReqAddAdmin(
    @Json(name = "correo") var correo: String,
    @Json(name = "nombres") var nombres: String,
    @Json(name = "apellidoPaterno") var apellidoPaterno: String,
    @Json(name = "apellidoMaterno") var apellidoMaterno: String,
)