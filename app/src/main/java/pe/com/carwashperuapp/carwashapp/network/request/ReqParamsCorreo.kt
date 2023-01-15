package pe.com.carwashperuapp.carwashapp.network.request

import com.squareup.moshi.Json

class ReqParamsCorreo(
    @Json(name = "address") val address: String,
    @Json(name = "pass") val pass: String,
)