package pe.com.carwashperuapp.carwashapp.network.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class ReqParamsCorreo(
    @Json(name = "address") val address: String,
    @Json(name = "pass") val pass: String,
)