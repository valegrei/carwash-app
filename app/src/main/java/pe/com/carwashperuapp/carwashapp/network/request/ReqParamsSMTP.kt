package pe.com.carwashperuapp.carwashapp.network.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class ReqParamsSMTP(
    @Json(name = "host") val host: String,
    @Json(name = "port") val port: String,
    @Json(name = "secure") val secure: String,
)