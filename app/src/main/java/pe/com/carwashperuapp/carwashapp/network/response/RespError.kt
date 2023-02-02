package pe.com.carwashperuapp.carwashapp.network.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class RespError(
    @Json(name = "message")
    val message: String = "",
)