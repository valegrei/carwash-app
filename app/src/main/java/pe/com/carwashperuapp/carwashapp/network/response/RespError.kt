package pe.com.carwashperuapp.carwashapp.network.response

import com.squareup.moshi.Json

class RespError(
    @Json(name = "message")
    val message: String = "",
)