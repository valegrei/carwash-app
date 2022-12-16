package pe.com.valegrei.carwashapp.network.request

import com.squareup.moshi.Json

class ReqParamsSMTP(
    @Json(name = "host") val host: String,
    @Json(name = "port") val port: String,
    @Json(name = "secure") val secure: String,
)